package com.benkao.tictactoe.ui.login

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.*

@LifecycleViewModel
class LoginViewModel(
    viewFinder: RxViewFinder
): RxViewModel(viewFinder) {

    override val streams: LifecycleStreams
        get() = LoginViewModel_StreamsInitializer.start(this)

    @InitToClear
    fun initToClear() = viewFinder
        .getRxView(R.id.login_error_text, RxTextView::class)
        .doOnSuccess {
            it.setVisibility(true)
            it.setText("Hello")
        }
        .ignoreElement()
}