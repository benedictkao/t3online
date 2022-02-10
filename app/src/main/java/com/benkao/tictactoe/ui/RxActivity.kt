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

abstract class RxActivity: DaggerAppCompatActivity() {
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    protected abstract var layout: Int

    private val createSubject = BehaviorSubject.createDefault(false)
    private val startSubject = BehaviorSubject.createDefault(false)
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        createSubject.onNext(true)
    }

    /**
     * Returns an object that extends RxViewModel based on the KClass input and
     * subscribes to its' annotated Completables via the Activity lifecycle
     */
    protected fun <T: RxViewModel> bindViewModel(clazz: KClass<T>): T {
        val viewModel = ViewModelProvider(this, providerFactory)
            .get(clazz.java)
        initStreams(viewModel)
        return viewModel
    }

    private fun <T: RxViewModel> initStreams(viewModel: T) {
        subscribeToCompletable(
            observeLifecycleStream(
                viewModel.createToDestroyCompletables,
                createSubject
            )
        )
        subscribeToCompletable(
            observeLifecycleStream(
                viewModel.startToStopCompletables,
                startSubject
            )
        )
    }

    /**
     * Returns an observable stream for the activity's create lifecycle.
     *
     * true = created, false = destroyed
     */
    fun observeCreateLifecycle() = createSubject.hide()

    /**
     * Returns an observable stream for the activity's start lifecycle.
     *
     * true = started, false = stopped
     */
    fun observeStartLifecycle() = startSubject.hide()

    private fun subscribeToCompletable(completable: Completable) {
        disposables.add(completable
            .subscribe({  }, { e -> e.printStackTrace() })
        )
        observeCreateLifecycle()
    }

    private fun observeLifecycleStream(
        completables: List<Completable>,
        lifecycleEvent: Observable<Boolean>
    ): Completable =
        lifecycleEvent
            .switchMapCompletable {
                if (it) {
                    Observable.fromIterable(completables)
                        .flatMapCompletable { it }
                } else {
                    Completable.complete()
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
        disposables.dispose()
        super.onDestroy()
    }
}