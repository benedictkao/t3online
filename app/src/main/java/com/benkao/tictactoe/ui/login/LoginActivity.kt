package com.benkao.tictactoe.ui.login

import android.os.Bundle
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.RxActivity

class LoginActivity : RxActivity() {
    private lateinit var viewModel: LoginViewModel

    override var layout: Int = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = bindViewModel(LoginViewModel::class)
    }
}