package com.benkao.tictactoe.ui.base

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.utils.completeOnError
import com.benkao.tictactoe.utils.subscribeAndAddTo
import com.benkao.tictactoe.utils.subscribeBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class RxViewModel(
    val screenNavigator: ScreenNavigator,
    val viewStream: RxViewStream? = null
) : ViewModel() {
    open val streams: LifecycleStreams? = null

    private var bindDisposable: Disposable? = null
    private val compositeDisposable = CompositeDisposable()
    private val bindSubject = PublishSubject.create<Boolean>()
    private val hideKeyboardSubject = PublishSubject.create<Boolean>()
    private val bundleSubject = BehaviorSubject.create<Bundle>()
    val hideKeyboardObservable: Observable<Boolean> = hideKeyboardSubject.hide()

    init {
        bindSubject.firstOrError()
            .flatMapCompletable {
                streams?.run {
                    Observable.fromIterable(initToClear)
                        .flatMapCompletable { it.completeOnError() }
                } ?: Completable.complete()
            }.subscribeAndAddTo(compositeDisposable)
    }

    /**
     * Subscribes this object's completable streams to the lifecycle source
     */
    fun observeLifecycleSource(lifecycleSource: RxLifecycleSource) {
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

    fun setData(bundle: Bundle) {
        bundleSubject.onNext(bundle)
    }

    /**
     * Returns a Single of the data type requested. Returns IllegalArgumentException
     * if the key is not found
     */
    fun <T: Parcelable> observeData(key: String): Single<T> =
        bundleSubject.firstOrError()
            .flatMap {
                it.getParcelable<T>(key)?.let { data ->
                    Single.just(data)
                } ?: Single.error(IllegalArgumentException())
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
                        .flatMapCompletable { it.completeOnError() }
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