package com.benkao.tictactoe.network.websocket

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.*
import timber.log.Timber

interface WebSocketProvider {

    fun observeState(): Observable<Int>

    fun observeConnection(): Observable<Boolean>

    fun connect()

    fun disconnect()
}

class WebSocketProviderImpl(
    private val client: OkHttpClient,
    private val request: Request
): WebSocketProvider, WebSocketListener() {

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val DEFAULT_CLOSE_MESSAGE = "Closing connection"
    }

    private var webSocket: WebSocket? = null
    private val state = BehaviorSubject.createDefault(WebSocketState.CLOSED)

    override fun observeState(): Observable<Int> = state.hide()

    override fun observeConnection(): Observable<Boolean> =
        state.map { it == WebSocketState.OPEN }
            .distinctUntilChanged()

    override fun connect() {
        webSocket ?: let {
            webSocket = client.newWebSocket(request, this)
        }
    }

    override fun disconnect() {
        webSocket?.run {
            close(NORMAL_CLOSURE_STATUS, DEFAULT_CLOSE_MESSAGE)
            webSocket = null
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("Websocket connected")
        state.onNext(WebSocketState.OPEN)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // handle message
        Timber.d("Websocket message received")
        println(text)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        this.webSocket = null
        state.onNext(WebSocketState.CLOSED)
        Timber.w("Websocket connection closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        this.webSocket = null
        state.onNext(WebSocketState.ERROR)
        Timber.e("Failed to connect to websocket")
    }
}