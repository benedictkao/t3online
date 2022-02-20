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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber

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
    private val statusText = viewCollector.addView(R.id.status_text, RxTextView::class)

    @InitToClear
    fun connectOrDisconnect(): Completable =
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
        statusText
            .flatMapCompletable { textView ->
                webSocketProvider.observeState()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { state ->
                        textView.setText(
                            when (state) {
                                WebSocketState.CONNECTING -> "CONNECTING"
                                WebSocketState.OPEN -> "CONNECTED"
                                WebSocketState.CLOSED -> "DISCONNECTED"
                                WebSocketState.ERROR -> "ERROR"
                                else -> "DEFAULT"
                            }
                        )
                    }
                    .ignoreElements()
            }

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
                    closeSubject.onNext(true)
                    userPreferences.clearUserId()
                    activityNavigator
                        .plan(LoginActivity::class)
                        .start(true)
                }
                .ignoreElements()
        }
}