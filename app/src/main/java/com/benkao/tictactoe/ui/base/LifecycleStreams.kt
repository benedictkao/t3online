package com.benkao.tictactoe.ui.base

import io.reactivex.rxjava3.core.Completable

data class LifecycleStreams(
    val createToDestroy: List<Completable>,
    val startToStop: List<Completable>,
)
