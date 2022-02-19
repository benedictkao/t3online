package com.benkao.tictactoe.network.retrofit.service

import com.benkao.tictactoe.network.retrofit.api.PlayerAvailApi
import io.reactivex.rxjava3.core.Completable

interface PlayerAvailService {

    fun findGame(): Completable
}

class PlayerAvailServiceImpl(private val api: PlayerAvailApi): PlayerAvailService {

    override fun findGame(): Completable {
        return api.findGame()
    }
}