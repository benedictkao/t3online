package com.benkao.tictactoe.di.core

import com.benkao.tictactoe.di.modules.HomeModule
import com.benkao.tictactoe.di.modules.LoginModule
import com.benkao.tictactoe.di.modules.SplashModule
import com.benkao.tictactoe.ui.main.HomeActivity
import com.benkao.tictactoe.ui.login.LoginActivity
import com.benkao.tictactoe.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [HomeModule::class])
    abstract fun contributeHomeActivity(): HomeActivity
}