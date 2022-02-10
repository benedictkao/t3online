package com.benkao.tictactoe.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.benkao.tictactoe.di.core.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class RxActivity: DaggerAppCompatActivity() {
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    protected abstract var layout: Int

    private val createSubject = BehaviorSubject.createDefault(false)
    private val startSubject = BehaviorSubject.createDefault(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        createSubject.onNext(true)
    }

    /**
     * Returns an object that extends RxViewModel and observes the Activity lifecycle
     */
    protected fun <T: RxViewModel> bindViewModel(clazz: KClass<T>): T {
        val viewModel = ViewModelProvider(this, providerFactory)
            .get(clazz.java)
        viewModel.observeActivityLifecycle(this)
        return viewModel
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