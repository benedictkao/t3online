package com.benkao.tictactoe.ui.login

import android.os.Bundle
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.RxActivity

class LoginActivity : RxActivity() {

    override var layout: Int? = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel(LoginViewModel::class)
    }
}