package com.benkao.tictactoe.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class RxViewModel(
    val viewFinder: RxViewFinder
) : ViewModel() {
    abstract val streamsContainer: LifecycleStreamsContainer

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun subscribeUntilClear(completables: List<Completable>) {
        subscribeToCompletable(
            Observable.fromIterable(completables)
                .flatMapCompletable { it }
        )
    }

    /**
     * Subscribes this object's completable streams to the lifecycle source
     */
    fun observeActivityLifecycle(lifecycleSource: RxLifecycleSource) {
        subscribeStreamsToLifecycleEvent(
            streamsContainer.getCreateToDestroyStreams(),
            lifecycleSource.observeCreateLifecycle()
        )
        subscribeStreamsToLifecycleEvent(
            streamsContainer.getStartToStopStreams(),
            lifecycleSource.observeStartLifecycle()
        )
    }

    private fun subscribeToCompletable(completable: Completable) {
        disposables.add(
            completable.subscribe({  }, { e -> e.printStackTrace() })
        )
    }

    private fun subscribeStreamsToLifecycleEvent(
        completables: List<Completable>,
        lifecycleEvent: Observable<Boolean>
    ) {
        subscribeToCompletable(
            lifecycleEvent
                .switchMapCompletable {
                    if (it) {
                        Observable.fromIterable(completables)
                            .flatMapCompletable { it }
                    } else {
                        Completable.complete()
                    }
                }
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}