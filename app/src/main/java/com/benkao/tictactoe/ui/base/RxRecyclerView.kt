package com.benkao.tictactoe.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class RxRecyclerView<VH: RecyclerView.ViewHolder>(
    resId: Int,
    val adapter: RecyclerView.Adapter<VH>,
    val layoutManager: RecyclerView.LayoutManager
) : RxBaseView(resId) {

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<RecyclerView>(resId).run {
            adapter = this@RxRecyclerView.adapter
            layoutManager = this@RxRecyclerView.layoutManager
        }
    }
}