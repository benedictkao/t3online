package com.benkao.tictactoe.di.core

import com.benkao.tictactoe.ui.base.RxViewCollector
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    fun provideRxViewFinder(): RxViewCollector {
        return RxViewCollector()
    }
}