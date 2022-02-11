package com.benkao.tictactoe.di.core

import com.benkao.tictactoe.ui.base.RxViewFinder
import com.benkao.tictactoe.ui.base.RxViewFinderImpl
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    fun provideRxViewFinder(): RxViewFinder {
        return RxViewFinderImpl()
    }
}