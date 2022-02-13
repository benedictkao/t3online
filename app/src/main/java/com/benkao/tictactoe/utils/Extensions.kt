package com.benkao.tictactoe.utils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Completable.subscribeBy(
    disposable: Disposable?,
    onComplete: () -> Unit = { },
    onError: (throwable: Throwable) -> Unit = { e -> e.printStackTrace() }
): Disposable {
    disposable?.takeIf { it.isDisposed }?.run { dispose() }
    return this.subscribe(onComplete, onError)
}

fun Completable.subscribeAndAddTo(
    compositeDisposable: CompositeDisposable,
    onComplete: () -> Unit = { },
    onError: (throwable: Throwable) -> Unit = { e -> e.printStackTrace() }
) {
    compositeDisposable.add(this.subscribe(onComplete, onError))
}