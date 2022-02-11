package com.benkao.tictactoe.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

abstract class RxViewModel(
    val viewFinder: RxViewFinder? = null
) : ViewModel() {
    private val mutableCreateToDestroyList = mutableListOf<Completable>()
    private val mutableStartToStopList = mutableListOf<Completable>()
    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        val memberFunctions = Class.forName(javaClass.name).kotlin.memberFunctions

        memberFunctions.forEach {
            it.run {
                takeIf {
                    returnType.isSubtypeOf(Completable::class.createType())
                }?.let {
                    // add create to destroy functions
                    takeIf {
                        annotations.any {it is CreateToDestroy }
                    }?.let {
                        mutableCreateToDestroyList.add(it.call(this@RxViewModel) as Completable)
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
    }

    /**
     * Subscribes this object's completable streams to the lifecycle source
     */
    fun observeActivityLifecycle(lifecycleSource: RxLifecycleSource) {
        subscribeToCompletable(
            observeLifecycleStream(
                mutableCreateToDestroyList,
                lifecycleSource.observeCreateLifecycle()
            )
        )
        subscribeToCompletable(
            observeLifecycleStream(
                mutableStartToStopList,
                lifecycleSource.observeStartLifecycle()
            )
        )
    }

    private fun subscribeToCompletable(completable: Completable) {
        disposables.add(
            completable.subscribe({  }, { e -> e.printStackTrace() })
        )
    }

    private fun observeLifecycleStream(
        completables: List<Completable>,
        lifecycleEvent: Observable<Boolean>
    ): Completable =
        lifecycleEvent
            .switchMapCompletable {
                if (it) {
                    Observable.fromIterable(completables)
                        .flatMapCompletable { it }
                } else {
                    Completable.complete()
                }
            }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}