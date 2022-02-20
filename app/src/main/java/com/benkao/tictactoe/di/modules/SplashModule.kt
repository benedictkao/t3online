package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.ActivityNavigator
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
        activityNavigator: ActivityNavigator
    ): ViewModel {
        return SplashViewModel(userPreferences, activityNavigator)
    }
}