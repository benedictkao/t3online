package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.network.retrofit.api.PlayerAvailApi
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailService
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailServiceImpl
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.ui.home.HomeViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
object HomeModule {

    @Provides
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun provideHomeViewModel(
        webSocketProvider: WebSocketProvider,
        playerAvailService: PlayerAvailService,
        viewCollector: RxViewCollector
    ): ViewModel {
        return HomeViewModel(webSocketProvider, playerAvailService, viewCollector)
    }

    @Provides
    fun providePlayerAvailApi(retrofit: Retrofit): PlayerAvailApi {
        return retrofit.create(PlayerAvailApi::class.java)
    }

    @Provides
    fun providePlayerAvailService(playerAvailApi: PlayerAvailApi): PlayerAvailService {
        return PlayerAvailServiceImpl(playerAvailApi)
    }
}