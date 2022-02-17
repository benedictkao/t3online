package com.benkao.tictactoe.network.retrofit.api

import com.benkao.tictactoe.network.retrofit.model.SingleUserData
import com.benkao.tictactoe.network.retrofit.model.UserData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ReqresApi {

    @GET("api/users/{id}")
    fun getUser(@Path("id") id: Int): Single<SingleUserData>
}