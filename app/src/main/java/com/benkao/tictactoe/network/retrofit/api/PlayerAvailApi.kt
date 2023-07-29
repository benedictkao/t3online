package com.benkao.tictactoe.network.retrofit.api

import com.benkao.tictactoe.network.retrofit.model.UserStateData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface PlayerAvailApi {

    @GET("/findGame")
    fun findGame(): Completable

    @GET("/getUserState")
    fun getUserState(): Single<UserStateData>
}