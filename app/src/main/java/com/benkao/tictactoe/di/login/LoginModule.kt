package com.benkao.tictactoe.di.login

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.ui.login.LoginViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object LoginModule {

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(): ViewModel {
        return LoginViewModel()
    }
}