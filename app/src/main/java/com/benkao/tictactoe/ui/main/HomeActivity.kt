package com.benkao.tictactoe.ui.main

import android.os.Bundle
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.RxActivity

class HomeActivity: RxActivity() {

    override var layout: Int? = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel(HomeViewModel::class)
    }
}