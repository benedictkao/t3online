package com.benkao.tictactoe.ui.splash

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.ScreenNavigator
import com.benkao.tictactoe.ui.base.LifecycleStreams
import com.benkao.tictactoe.ui.base.RxViewModel
import com.benkao.tictactoe.ui.main.HomeActivity
import com.benkao.tictactoe.ui.login.LoginActivity
import io.reactivex.rxjava3.core.Completable

@LifecycleViewModel
class SplashViewModel(
    private val userPreferences: UserPreferences,
    screenNavigator: ScreenNavigator
): RxViewModel(screenNavigator) {

    override val streams: LifecycleStreams
        get() = SplashViewModel_LifecycleStreamsFactory.create(this)

    @InitToClear
    fun observeLoginStatus(): Completable =
        userPreferences.getUserId()
            .doOnSuccess {
                screenNavigator
                    .planActivity(when {
                        it.isBlank() -> LoginActivity::class
                        else -> HomeActivity::class
                    }).start(true)
            }
            .ignoreElement()
}