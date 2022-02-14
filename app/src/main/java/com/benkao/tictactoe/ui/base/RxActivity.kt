package com.benkao.tictactoe.ui.base

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.benkao.tictactoe.di.core.ViewModelProviderFactory
import com.benkao.tictactoe.utils.subscribeAndAddTo
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class RxActivity: DaggerAppCompatActivity(), RxLifecycleSource {
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    protected abstract var layout: Int

    private var compositeDisposable = CompositeDisposable()
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
        viewModel.hideKeyboardObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { hideKeyboard() }
            .ignoreElements()
            .subscribeAndAddTo(compositeDisposable)

        bindViews(viewModel.viewStream)

        return viewModel
    }

    /**
     * Hides the soft keyboard
     */
    fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.run {
                currentFocus?.windowToken?.let {
                    hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
    }

    private fun bindViews(viewStream: RxViewStream?) {
        viewStream?.run {
            observeStream()
                .doOnNext { it.bind(this@RxActivity) }
                .ignoreElements()
                .subscribeAndAddTo(compositeDisposable)
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
        compositeDisposable.dispose()
        super.onDestroy()
    }
}