package com.benkao.tictactoe.network.retrofit.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData (
    @Json(name = "userId") val id: String,
    @Json(name = "nickname") val nickname: String
)