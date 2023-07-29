package com.benkao.tictactoe.ui.main

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.tictactoe.R
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailService
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.*
import com.benkao.tictactoe.ui.home.HomeViewModel_LifecycleStreamsFactory
import com.benkao.tictactoe.ui.login.LoginActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

@LifecycleViewModel
class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val webSocketProvider: WebSocketProvider,
    private val service: PlayerAvailService,
    screenNavigator: ScreenNavigator,
    viewCollector: RxViewCollector
): RxViewModel(screenNavigator, viewCollector) {

    override val streams: LifecycleStreams
        get() = HomeViewModel_LifecycleStreamsFactory.create(this)

    private val playButton = viewCollector.addView(R.id.play_button, RxButton::class)
    private val logoutButton = viewCollector.addView(R.id.logout_button, RxButton::class)

    @InitToClear
    fun observeFindButtonState(): Completable =
        playButton.flatMapCompletable { button ->
            webSocketProvider.observeConnection()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { button.setEnabled(it) }
                .ignoreElements()
        }

    @InitToClear
    fun observeFindGameClick(): Completable =
        playButton.flatMapCompletable {
            it.observeClick()
                .switchMapCompletable {
                    service.findGame()
                }
        }

    @InitToClear
    fun observeLogout(): Completable =
        logoutButton.flatMapCompletable {
            it.observeClick()
                .doOnNext {
                    userPreferences.clearUserId()
                    screenNavigator
                        .planActivity(LoginActivity::class)
                        .start(true)
                }
                .ignoreElements()
        }
}