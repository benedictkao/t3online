package com.benkao.tictactoe.network.retrofit.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameInfo(
    @Json(name = "gameId") val gameId: Int,
    @Json(name = "opponent") val opponent: UserData,
    @Json(name = "board") val board: List<Int>,
    @Json(name = "isTurn") val isTurn: Boolean
)