package com.benkao.tictactoe.di.core

import androidx.lifecycle.ViewModelProvider
import com.benkao.tictactoe.di.core.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {

    @Binds
    fun bindViewModelFactory(modelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}