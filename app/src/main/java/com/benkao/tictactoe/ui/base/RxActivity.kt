package com.benkao.tictactoe.ui.base

import android.content.Context
import android.content.Intent
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

    protected open var layout: Int? = null

    private var compositeDisposable = CompositeDisposable()
    private val createSubject = BehaviorSubject.createDefault(false)
    private val startSubject = BehaviorSubject.createDefault(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout?.let { setContentView(it) }
        createSubject.onNext(true)
    }

    /**
     * Returns a RxViewModel type object that observes the Activity lifecycle
     */
    protected fun <T: RxViewModel> bindViewModel(clazz: KClass<T>): T {
        val viewModel = ViewModelProvider(this, providerFactory)
            .get(clazz.java)

        viewModel.observeActivityLifecycle(this)
        observeViewModel(viewModel)

        bindViews(viewModel.viewStream)

        return viewModel
    }

    private fun observeViewModel(viewModel: RxViewModel) {
        viewModel.hideKeyboardObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { hideKeyboard() }
            .ignoreElements()
            .subscribeAndAddTo(compositeDisposable)
        viewModel.activityNavigator
            .observePlans()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { executeActivityPlan(it) }
            .ignoreElements()
            .subscribeAndAddTo(compositeDisposable)
    }

    private fun executeActivityPlan(activityPlan: ActivityPlan) {
        activityPlan.run {
            clazz?.let {
                val intent = Intent(this@RxActivity, it.java)
                flags?.let { intent.addFlags(it) }
                data?.run { intent.putExtra(first, second) }
                startActivity(intent)
            }
            if (finishCurrent) {
                finish()
            }
        }
    }

    /**
     * Hides the soft keyboard
     */
    private fun hideKeyboard() {
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