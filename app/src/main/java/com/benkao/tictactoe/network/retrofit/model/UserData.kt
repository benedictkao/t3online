package com.benkao.tictactoe.network.retrofit.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String?,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?
)
