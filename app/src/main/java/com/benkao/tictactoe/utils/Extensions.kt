package com.benkao.tictactoe.utils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

fun Completable.subscribeBy(
    disposable: Disposable?,
    onComplete: () -> Unit = { },
    onError: (throwable: Throwable) -> Unit = { Timber.e(it.message) }
): Disposable {
    disposable?.takeIf { it.isDisposed }?.run { dispose() }
    return this.subscribe(onComplete, onError)
}

fun Completable.subscribeAndAddTo(
    compositeDisposable: CompositeDisposable,
    onComplete: () -> Unit = { },
    onError: (throwable: Throwable) -> Unit = { Timber.e(it.message) }
) {
    compositeDisposable.add(this.subscribe(onComplete, onError))
}

fun Completable.completeOnError(): Completable {
    return this.doOnError { Timber.e(it.message) }.onErrorComplete()
}