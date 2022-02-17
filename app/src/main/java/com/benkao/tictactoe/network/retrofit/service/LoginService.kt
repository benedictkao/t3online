package com.benkao.tictactoe.network.retrofit.service

import com.benkao.tictactoe.network.retrofit.api.ReqresApi
import com.benkao.tictactoe.network.retrofit.model.User
import io.reactivex.rxjava3.core.Single

interface LoginService {

    fun postLogin(id: Int): Single<User>
}

class LoginServiceImpl(private val api: ReqresApi): LoginService {

    override fun postLogin(id: Int): Single<User> {
        return api.getUser(id)
            .map { it.data.run {
                User(
                    id,
                    email ?: "email",
                    firstName ?: "firstname",
                    lastName ?: "lastname"
                )
            } }
    }
}