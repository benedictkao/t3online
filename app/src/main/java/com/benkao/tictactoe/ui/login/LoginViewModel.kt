package com.benkao.tictactoe.ui.login

import com.benkao.tictactoe.ui.base.CreateToDestroy
import com.benkao.tictactoe.ui.base.RxViewModel
import com.benkao.tictactoe.ui.base.StartToStop
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class LoginViewModel: RxViewModel() {

    @CreateToDestroy
    fun test() = Completable.fromAction { println("Create to Destroy") }

    @StartToStop
    fun hi() = Observable.just("Start To Stop")
        .doOnNext { println(it) }
        .ignoreElements()
}