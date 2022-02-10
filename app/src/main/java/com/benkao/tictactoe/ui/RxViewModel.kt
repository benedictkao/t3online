package com.benkao.tictactoe.ui

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Completable
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

abstract class RxViewModel : ViewModel() {
    val createToDestroyCompletables: List<Completable>
    val startToStopCompletables: List<Completable>

    init {
        val memberFunctions = Class.forName(javaClass.name).kotlin.memberFunctions

        val mutableOpenToCloseList = mutableListOf<Completable>()
        val mutableStartToStopList = mutableListOf<Completable>()
        memberFunctions.forEach {
            it.run {
                takeIf {
                    returnType.isSubtypeOf(Completable::class.createType())
                }?.let {
                    // add open to close functions
                    takeIf {
                        annotations.any {it is CreateToDestroy }
                    }?.let {
                        mutableOpenToCloseList.add(it.call(this@RxViewModel) as Completable)
                    }

                    // add start to stop functions
                    takeIf {
                        annotations.any {it is StartToStop }
                    }?.let {
                        mutableStartToStopList.add(it.call(this@RxViewModel) as Completable)
                    }
                }
            }
        }
        createToDestroyCompletables = mutableOpenToCloseList.toList()
        startToStopCompletables = mutableStartToStopList.toList()
    }
}