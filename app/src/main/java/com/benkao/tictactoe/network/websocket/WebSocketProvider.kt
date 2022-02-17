package com.benkao.tictactoe.network.websocket

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.*

interface WebSocketProvider {

    fun observeState(): Observable<Int>

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
    private val state = BehaviorSubject.createDefault(WebSocketState.DISCONNECTED)

    override fun observeState(): Observable<Int> = state.hide()

    override fun connect() {
        if (state.value == WebSocketState.DISCONNECTED) {
            state.onNext(WebSocketState.CONNECTING)
            webSocket = client.newWebSocket(request, this)
        }
    }

    override fun disconnect() {
        webSocket?.run { close(NORMAL_CLOSURE_STATUS, DEFAULT_CLOSE_MESSAGE) }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        state.onNext(WebSocketState.CONNECTED)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // handle message
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        state.onNext(WebSocketState.DISCONNECTED)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        state.onNext(WebSocketState.DISCONNECTED)
    }
}