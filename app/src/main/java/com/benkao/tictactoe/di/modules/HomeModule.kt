package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.network.retrofit.api.PlayerAvailApi
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailService
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailServiceImpl
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.ActivityNavigator
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
        userPreferences: UserPreferences,
        webSocketProvider: WebSocketProvider,
        playerAvailService: PlayerAvailService,
        activityNavigator: ActivityNavigator,
        viewCollector: RxViewCollector
    ): ViewModel {
        return HomeViewModel(
            userPreferences,
            webSocketProvider,
            playerAvailService,
            activityNavigator,
            viewCollector
        )
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