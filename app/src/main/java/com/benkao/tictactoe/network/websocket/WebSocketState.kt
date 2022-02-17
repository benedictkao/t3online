package com.benkao.tictactoe.network.websocket

import androidx.annotation.IntDef

@IntDef(
    WebSocketState.DISCONNECTED,
    WebSocketState.CONNECTING,
    WebSocketState.CONNECTED
)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocketState {
    companion object {
        const val DISCONNECTED = 0
        const val CONNECTING = 1
        const val CONNECTED = 2
    }
}
