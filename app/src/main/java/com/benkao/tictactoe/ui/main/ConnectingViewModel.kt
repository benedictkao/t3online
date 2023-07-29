package com.benkao.tictactoe.ui.main

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.benkao.tictactoe.R
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.network.websocket.WebSocketState
import com.benkao.tictactoe.ui.base.ScreenNavigator
import com.benkao.tictactoe.ui.base.RxTextView
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.ui.base.RxViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

@LifecycleViewModel
class ConnectingViewModel(
    private val webSocketProvider: WebSocketProvider,
    screenNavigator: ScreenNavigator,
    viewCollector: RxViewCollector
): RxViewModel(screenNavigator, viewCollector) {

    private val statusText = viewCollector.addView(R.id.status_text, RxTextView::class)
    private val shouldConnectSubject = BehaviorSubject.createDefault(true)

    @InitToClear
    fun manageConnection(): Completable =
        Observable.combineLatest(
            shouldConnectSubject.distinctUntilChanged(),
            webSocketProvider.observeConnection()
        ) { shouldConnect, isConnected -> Pair(shouldConnect, isConnected) }
            .filter { it.first != it.second }
            .observeOn(Schedulers.io())
            .doOnNext {
                webSocketProvider.run {
                    if (it.first) connect() else disconnect()
                }
            }
            .ignoreElements()

    @StartToStop
    fun observeConnectionState(): Completable =
        statusText
            .flatMapCompletable { textView ->
                webSocketProvider.observeState()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { state ->
                        textView.setText(
                            when (state) {
                                WebSocketState.CONNECTING -> "CONNECTING" //show connecting to server
                                WebSocketState.OPEN -> "CONNECTED" //get game state
                                WebSocketState.CLOSED -> "DISCONNECTED"
                                WebSocketState.ERROR -> "ERROR" //show failed to connect to server
                                else -> "DEFAULT"
                            }
                        )
                    }
                    .ignoreElements()
            }
}