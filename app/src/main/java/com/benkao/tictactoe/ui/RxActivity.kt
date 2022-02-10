package com.benkao.tictactoe.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.benkao.tictactoe.di.core.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

abstract class RxActivity: DaggerAppCompatActivity() {
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    protected abstract var layout: Int

    private val createSubject = BehaviorSubject.createDefault(false)
    private val startSubject = BehaviorSubject.createDefault(false)
    private val onStopDisposables: CompositeDisposable = CompositeDisposable()
    private val onDestroyDisposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        createSubject.onNext(true)
    }

    protected fun <T: RxViewModel> bindViewModel(clazz: KClass<T>): T {
        val viewModel = ViewModelProvider(this, providerFactory)
            .get(clazz.java)

        observeCompletable(createToDestroyStream(viewModel), onDestroyDisposables)
        observeCompletable(startToStopStream(viewModel), onDestroyDisposables)

        return viewModel
    }

    private fun <T: RxViewModel> getCompletables(viewModel: T, ) =
        Observable.fromIterable(viewModel::class.memberFunctions)
            .filter { it.returnType.isSubtypeOf(Completable::class.createType()) }
            .map { Pair(it.annotations, it.call(viewModel) as Completable) }

    private fun observeCompletable(completable: Completable, disposables: CompositeDisposable) {
        disposables.add(completable
            .subscribe({  }, { e -> e.printStackTrace() })
        )
    }

    private fun <T: RxViewModel> createToDestroyStream(viewModel: T) =
        getCompletables(viewModel)
            .filter { it.first.any { it is CreateToDestroy } }
            .map { it.second }
            .toList()
            .flatMapCompletable { completables ->
                createSubject.switchMapCompletable { created ->
                    if (created) {
                        Observable.fromIterable(completables)
                            .doOnNext { observeCompletable(it, onDestroyDisposables) }
                            .ignoreElements()
                    } else {
                        Completable.fromAction { onDestroyDisposables.dispose() }
                    }
                }
            }

    private fun <T: RxViewModel> startToStopStream(viewModel: T) =
        getCompletables(viewModel)
            .filter { it.first.any { it is StartToStop } }
            .map { it.second }
            .toList()
            .flatMapCompletable { completables ->
                startSubject.switchMapCompletable { started ->
                    if (started) {
                        Observable.fromIterable(completables)
                            .doOnNext { observeCompletable(it, onStopDisposables) }
                            .ignoreElements()
                    } else {
                        Completable.fromAction { onStopDisposables.clear() }
                    }
                }
            }

    override fun onStart() {
        startSubject.onNext(true)
        super.onStart()
    }

    override fun onStop() {
        startSubject.onNext(false)
        super.onStop()
    }

    override fun onDestroy() {
        createSubject.onNext(false)
        super.onDestroy()
    }
}