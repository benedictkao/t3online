package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.ui.home.HomeViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object HomeModule {

    @Provides
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun provideHomeViewModel(
        webSocketProvider: WebSocketProvider,
        viewCollector: RxViewCollector
    ): ViewModel {
        return HomeViewModel(webSocketProvider, viewCollector)
    }
}