package com.benkao.tictactoe.network.retrofit.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStateData(
    @Json(name = "state") val state: String,
    @Json(name = "gameInfo") val gameInfo: GameInfo?
)
