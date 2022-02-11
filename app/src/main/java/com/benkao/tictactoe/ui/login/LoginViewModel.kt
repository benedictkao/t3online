package com.benkao.tictactoe.ui.login

import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.RxTextView
import com.benkao.tictactoe.ui.base.RxViewFinder
import com.benkao.tictactoe.ui.base.annotations.CreateToDestroy
import com.benkao.tictactoe.ui.base.RxViewModel
import com.benkao.tictactoe.ui.base.annotations.InitToClear
import com.benkao.tictactoe.ui.base.annotations.StartToStop
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class LoginViewModel(
    viewFinder: RxViewFinder
): RxViewModel(viewFinder) {

    @InitToClear
    fun initToClear() = viewFinder
        .getRxView(R.id.login_error_text, RxTextView::class)
        .doOnSuccess {
            it.setVisibility(true)
            it.setText("Hello")
        }
        .ignoreElement()

    @CreateToDestroy
    fun test() = Completable.fromAction { println("Create to Destroy") }

    @StartToStop
    fun hi() = Observable.just("Start To Stop")
        .doOnNext { println(it) }
        .ignoreElements()
}