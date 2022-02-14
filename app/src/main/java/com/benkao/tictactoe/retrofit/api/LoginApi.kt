package com.benkao.tictactoe.retrofit.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.POST

interface LoginApi {

    @POST
    fun postLoginAttempt(): Single<String>
}