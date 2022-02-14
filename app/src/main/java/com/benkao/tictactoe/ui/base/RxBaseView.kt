package com.benkao.tictactoe.ui.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class RxBaseView(@IdRes val resId: Int) {
    private val visibility = MutableLiveData<Int>()
    private val enabled = MutableLiveData<Boolean>()

    private val focusSubject = BehaviorSubject.create<Boolean>()
    private val clickSubject = PublishSubject.create<Boolean>()

    fun setVisible(visible: Boolean) {
        this.visibility.value = if (visible) View.VISIBLE else View.GONE
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled.value = enabled
    }

    fun observeFocus(): Observable<Boolean> = focusSubject.distinctUntilChanged().hide()
    
    fun observeClick(): Observable<Boolean> = clickSubject.hide()

    @CallSuper
    open fun bind(activity: AppCompatActivity) {
        activity.findViewById<View>(resId).run {
            this@RxBaseView.visibility.observe(activity) {
                it?.let { visibility = it }
            }
            this@RxBaseView.enabled.observe(activity) {
                it?.let { isEnabled = it }
            }
            setOnFocusChangeListener { _, focus -> focusSubject.onNext(focus) }
            setOnClickListener { clickSubject.onNext(true) }
        }
    }
}