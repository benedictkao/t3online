package com.benkao.tictactoe.network.retrofit.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginData (
    @Json val userId: String
)