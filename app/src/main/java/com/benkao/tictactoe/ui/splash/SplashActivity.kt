package com.benkao.tictactoe.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import com.benkao.tictactoe.ui.base.RxActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity: RxActivity() {
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = bindViewModel(SplashViewModel::class)
    }
}