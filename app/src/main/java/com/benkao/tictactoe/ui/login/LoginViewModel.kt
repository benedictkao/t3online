package com.benkao.tictactoe.ui.login

import com.benkao.tictactoe.ui.CreateToDestroy
import com.benkao.tictactoe.ui.RxViewModel
import com.benkao.tictactoe.ui.StartToStop
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