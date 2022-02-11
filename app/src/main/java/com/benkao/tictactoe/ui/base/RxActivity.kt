package com.benkao.tictactoe.ui.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.benkao.tictactoe.di.core.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class RxActivity: DaggerAppCompatActivity(), RxLifecycleSource {
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    protected abstract var layout: Int

    private var viewFinderDisposable: Disposable? = null
    private val createSubject = BehaviorSubject.createDefault(false)
    private val startSubject = BehaviorSubject.createDefault(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        createSubject.onNext(true)
    }

    /**
     * Returns a RxViewModel type object that observes the Activity lifecycle
     */
    protected fun <T: RxViewModel> bindViewModel(clazz: KClass<T>): T {
        val viewModel = ViewModelProvider(this, providerFactory)
            .get(clazz.java)

        viewModel.observeActivityLifecycle(this)
        bindViews(viewModel.viewFinder)

        return viewModel
    }

    private fun bindViews(viewFinder: RxViewFinder) {
        viewFinder.run {
            viewFinderDisposable = observeViews()
                .doOnNext { it.bind(this@RxActivity) }
                .subscribe()
        }
    }

    override fun observeCreateLifecycle(): Observable<Boolean> = createSubject.hide()

    override fun observeStartLifecycle(): Observable<Boolean> = startSubject.hide()

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
        viewFinderDisposable?.dispose()
        super.onDestroy()
    }
}