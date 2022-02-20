package com.benkao.tictactoe.ui.home

import android.os.Bundle
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.RxActivity

class HomeActivity: RxActivity() {
    private lateinit var viewModel: HomeViewModel

    override var layout: Int? = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel(HomeViewModel::class)
    }
}