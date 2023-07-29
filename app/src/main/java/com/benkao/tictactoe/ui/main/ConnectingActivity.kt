package com.benkao.tictactoe.ui.main

import android.os.Bundle
import com.benkao.tictactoe.ui.base.RxActivity

class ConnectingActivity: RxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel(ConnectingViewModel::class)
    }
}