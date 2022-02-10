package com.benkao.tictactoe.ui.login

import com.benkao.tictactoe.ui.CreateToDestroy
import com.benkao.tictactoe.ui.RxViewModel
import com.benkao.tictactoe.ui.StartToStop
import io.reactivex.rxjava3.core.Completable

class LoginViewModel: RxViewModel() {

    @CreateToDestroy
    internal fun test() = Completable.fromAction { System.out.println("Create to destroy") }

    @StartToStop
    internal fun hi() = Completable.fromAction { System.out.println("Start To Stop") }
}