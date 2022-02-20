package com.benkao.tictactoe.network.websocket

import androidx.annotation.IntDef

@IntDef(
    WebSocketState.CLOSED,
    WebSocketState.CONNECTING,
    WebSocketState.OPEN,
    WebSocketState.ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocketState {
    companion object {
        const val CLOSED = 0
        const val CONNECTING = 1
        const val OPEN = 2
        const val ERROR = 3
    }
}
