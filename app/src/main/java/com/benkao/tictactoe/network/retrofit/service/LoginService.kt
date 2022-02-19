package com.benkao.tictactoe.network.retrofit.service

import com.benkao.tictactoe.network.retrofit.api.LoginApi
import io.reactivex.rxjava3.core.Single

interface LoginService {

    fun postLogin(): Single<String>
}

class LoginServiceImpl(private val api: LoginApi): LoginService {

    override fun postLogin(): Single<String> {
        return api.login()
            .map { it.userId }
    }
}