package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.ScreenNavigator
import com.benkao.tictactoe.ui.splash.SplashViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object SplashModule {

    @Provides
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun provideSplashViewModel(
        userPreferences: UserPreferences,
        screenNavigator: ScreenNavigator
    ): ViewModel {
        return SplashViewModel(userPreferences, screenNavigator)
    }
}