package com.benkao.tictactoe.ui.base

import com.benkao.tictactoe.ui.login.LoginViewModel
import io.reactivex.rxjava3.core.Completable

class LoginViewModel_StreamsContainer private constructor(
    private val createToDestroyStreams: List<Completable>,
    private val startToStopStreams: List<Completable>,
): LifecycleStreamsContainer {

    companion object {
        fun init(viewModel: LoginViewModel): LoginViewModel_StreamsContainer {
            viewModel.subscribeUntilClear(listOf(
                viewModel.initToClear(),
            ))

            return LoginViewModel_StreamsContainer(
                listOf(),
                listOf()
            )
        }
    }

    override fun getCreateToDestroyStreams(): List<Completable> = createToDestroyStreams

    override fun getStartToStopStreams(): List<Completable> = startToStopStreams
}