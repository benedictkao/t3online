package com.benkao.tictactoe.ui.base

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

open class RxBaseView(@IdRes val resId: Int) {
    private val visibility = MutableLiveData(View.GONE)

    private val clickSubject = PublishSubject.create<Boolean>()

    fun setVisibility(visible: Boolean) {
        this.visibility.value = if (visible) View.VISIBLE else View.GONE
    }

    fun observeClick(): Observable<Boolean> = clickSubject.hide()

    @CallSuper
    open fun bind(activity: AppCompatActivity) {
        activity.findViewById<View>(resId).run {
            this@RxBaseView.visibility.observe(activity) {
                it?.let { visibility = it }
            }
            setOnClickListener { clickSubject.onNext(true) }
        }
    }
}

open class RxView(resId: Int): RxBaseView(resId)

open class RxTextView(resId: Int): RxView(resId) {
    private val text = MutableLiveData<String>()

    fun setText(text: String) {
        this.text.value = text
    }

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<TextView>(resId).run {
            this@RxTextView.text.observe(activity) {
                it?.let { text = it }
            }
        }
    }
}

open class RxImageView(resId: Int): RxView(resId) {
    private val imageRes = MutableLiveData<Int>()

    fun setImageRes(@DrawableRes imageRes: Int) {
        this.imageRes.value = imageRes
    }

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<ImageView>(resId).run {
            imageRes.observe(activity) {
                it?.let { setImageResource(it) }
            }
        }
    }
}

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

@IntDef(
    ViewType.VIEW,
    ViewType.TEXT_VIEW,
    ViewType.IMAGE_VIEW
)
annotation class ViewType {
    companion object {
        const val VIEW = 0
        const val TEXT_VIEW = 1
        const val IMAGE_VIEW = 2
    }
}