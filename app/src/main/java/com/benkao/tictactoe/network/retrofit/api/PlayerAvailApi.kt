package com.benkao.tictactoe.network.retrofit.api

import io.reactivex.rxjava3.core.Completable
import retrofit2.http.GET

interface PlayerAvailApi {

    @GET("/findGame")
    fun findGame(): Completable
}