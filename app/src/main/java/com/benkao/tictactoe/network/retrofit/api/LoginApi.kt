package com.benkao.tictactoe.network.retrofit.api

import com.benkao.tictactoe.network.retrofit.model.UserData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface LoginApi {

    @GET("/login")
    fun login(): Single<UserData>
}