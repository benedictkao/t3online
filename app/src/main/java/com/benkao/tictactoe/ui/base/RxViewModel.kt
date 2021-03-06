package com.benkao.tictactoe.ui.base

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.utils.subscribeAndAddTo
import com.benkao.tictactoe.utils.subscribeBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class RxViewModel(
    val activityNavigator: ActivityNavigator,
    val viewStream: RxViewStream? = null
) : ViewModel() {
    open val streams: LifecycleStreams? = null

    private var bindDisposable: Disposable? = null
    private val compositeDisposable = CompositeDisposable()
    private val bindSubject = PublishSubject.create<Boolean>()
    private val hideKeyboardSubject = PublishSubject.create<Boolean>()
    val hideKeyboardObservable: Observable<Boolean> = hideKeyboardSubject.hide()

    init {
        bindSubject.firstOrError()
            .flatMapCompletable {
                streams?.run {
                    Observable.fromIterable(initToClear)
                        .flatMapCompletable { it }
                } ?: Completable.complete()
            }.subscribeAndAddTo(compositeDisposable)
    }

    /**
     * Subscribes this object's completable streams to the lifecycle source
     */
    fun observeActivityLifecycle(lifecycleSource: RxLifecycleSource) {
        bindSubject.onNext(true)

        streams?.run {
            Completable.mergeArray(
                observeLifecycleEvent(
                    createToDestroy,
                    lifecycleSource.observeCreateLifecycle()
                ),
                observeLifecycleEvent(
                    startToStop,
                    lifecycleSource.observeStartLifecycle()
                )
            ).subscribeBy(bindDisposable)
        }
    }

    /**
     * Hides the keyboard of the RxActivity bound to this object
     */
    protected fun hideKeyboard() {
        hideKeyboardSubject.onNext(true)
    }

    private fun observeLifecycleEvent(
        completables: List<Completable>,
        lifecycleEvent: Observable<Boolean>
    ): Completable =
        lifecycleEvent
            .switchMapCompletable { event ->
                if (event) {
                    Observable.fromIterable(completables)
                        .flatMapCompletable { it }
                } else {
                    Completable.complete()
                }
            }

    override fun onCleared() {
        bindDisposable?.dispose()
        compositeDisposable.dispose()
        super.onCleared()
    }
}