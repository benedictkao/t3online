package com.benkao.tictactoe.ui.base

import io.reactivex.rxjava3.core.Completable

data class LifecycleStreams(
    val initToClear: List<Completable>,
    val createToDestroy: List<Completable>,
    val startToStop: List<Completable>
)
