package com.benkao.tictactoe.ui.home

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.benkao.tictactoe.R
import com.benkao.tictactoe.network.retrofit.service.PlayerAvailService
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.network.websocket.WebSocketState
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.*
import com.benkao.tictactoe.ui.login.LoginActivity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

@LifecycleViewModel
class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val webSocketProvider: WebSocketProvider,
    private val service: PlayerAvailService,
    activityNavigator: ActivityNavigator,
    viewCollector: RxViewCollector
): RxViewModel(activityNavigator, viewCollector) {

    override val streams: LifecycleStreams
        get() = HomeViewModel_LifecycleStreamsFactory.create(this)

    private val closeSubject = BehaviorSubject.createDefault(false)
    private val playButton = viewCollector.addView(R.id.play_button, RxButton::class)
    private val logoutButton = viewCollector.addView(R.id.logout_button, RxButton::class)

    @InitToClear
    fun connectToWebSocket(): Completable =
        Observable.combineLatest(
            webSocketProvider.observeConnection(),
            closeSubject.distinctUntilChanged()
        ) { isConnected, shouldClose -> Pair(isConnected, shouldClose) }
            .filter { it.first == it.second }
            .observeOn(Schedulers.io())
            .doOnNext {
                webSocketProvider.run {
                    if (it.first) disconnect() else connect()
                }
            }
            .ignoreElements()

    @StartToStop
    fun observeConnectionState(): Completable =
        webSocketProvider.observeState()
            .doOnNext {
                when (it) {
                    WebSocketState.OPEN -> println("Connected to t3 server")
                    WebSocketState.CLOSED -> println("Disconnected from server")
                    WebSocketState.ERROR -> println("Failed connecting to t3 server")
                }
            }
            .ignoreElements()

    @InitToClear
    fun observeFindGame(): Completable =
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
                    closeSubject.onNext(true)
                    userPreferences.clearUserId()
                    activityNavigator
                        .plan(LoginActivity::class)
                        .start(true)
                }
                .ignoreElements()
        }
}