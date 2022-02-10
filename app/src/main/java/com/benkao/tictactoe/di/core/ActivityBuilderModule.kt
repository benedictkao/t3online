package com.benkao.tictactoe.di.core

import com.benkao.tictactoe.ui.login.LoginActivity
import com.benkao.tictactoe.di.login.LoginModule
import com.benkao.tictactoe.ui.RxActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(
        modules = [LoginModule::class]
    )
    abstract fun contributeMainActivity(): LoginActivity

    abstract fun contributeRxActivity(): RxActivity
}