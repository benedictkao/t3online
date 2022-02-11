package com.benkao.tictactoe.ui.base

import io.reactivex.rxjava3.core.Observable

interface RxLifecycleSource {

    /**
     * Returns an observable stream for the source's create lifecycle.
     *
     * true = created, false = destroyed
     */
    fun observeCreateLifecycle(): Observable<Boolean>

    /**
     * Returns an observable stream for the source's start lifecycle.
     *
     * true = started, false = stopped
     */
    fun observeStartLifecycle(): Observable<Boolean>
}