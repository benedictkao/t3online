package com.benkao.tictactoe.ui.home

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.benkao.tictactoe.R
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.network.websocket.WebSocketState
import com.benkao.tictactoe.ui.base.LifecycleStreams
import com.benkao.tictactoe.ui.base.RxButton
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.ui.base.RxViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

@LifecycleViewModel
class HomeViewModel(
    private val webSocketProvider: WebSocketProvider,
    viewCollector: RxViewCollector
): RxViewModel(viewCollector) {

    override val streams: LifecycleStreams
        get() = HomeViewModel_LifecycleStreamsFactory.create(this)

    private val playButton = viewCollector.addView(R.id.play_button, RxButton::class)

    @InitToClear
    fun connectToWebSocket(): Completable =
        Completable.fromAction { webSocketProvider.connect() }
            .subscribeOn(Schedulers.io())

    @StartToStop
    fun observeConnectionState(): Completable =
        webSocketProvider.observeState()
            .doOnNext {
                when (it) {
                    WebSocketState.CONNECTING -> println("Connecting to t3 server...")
                    WebSocketState.CONNECTED -> println("Connected to t3 server")
                    WebSocketState.DISCONNECTED -> println("Disconnected from server")
                }
            }
            .ignoreElements()
}