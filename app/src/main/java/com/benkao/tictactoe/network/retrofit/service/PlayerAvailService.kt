package com.benkao.tictactoe.network.retrofit.service

import com.benkao.tictactoe.network.retrofit.api.PlayerAvailApi
import com.benkao.tictactoe.network.retrofit.model.UserStateData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PlayerAvailService {

    fun findGame(): Completable

    fun getUserState(): Single<UserStateData>
}

class PlayerAvailServiceImpl(private val api: PlayerAvailApi): PlayerAvailService {

    override fun findGame(): Completable {
        return api.findGame()
    }

    override fun getUserState(): Single<UserStateData> {
        return api.getUserState()
    }
}