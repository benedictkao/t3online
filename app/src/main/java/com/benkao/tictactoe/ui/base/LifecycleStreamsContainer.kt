package com.benkao.tictactoe.ui.base

import io.reactivex.rxjava3.core.Completable

interface LifecycleStreamsContainer {

    fun getCreateToDestroyStreams(): List<Completable>

    fun getStartToStopStreams(): List<Completable>
}
