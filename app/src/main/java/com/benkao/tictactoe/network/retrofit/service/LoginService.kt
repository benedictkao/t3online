package com.benkao.tictactoe.network.retrofit.service

import com.benkao.tictactoe.network.retrofit.api.LoginApi
import io.reactivex.rxjava3.core.Single

interface LoginService {

    fun login(): Single<String>
}

class LoginServiceImpl(private val api: LoginApi): LoginService {

    override fun login(): Single<String> {
        return api.login()
            .map { it.id }
    }
}