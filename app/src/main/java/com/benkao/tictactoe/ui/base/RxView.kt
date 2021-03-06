package com.benkao.tictactoe.ui.base

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.benkao.tictactoe.utils.StringUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

open class RxView(resId: Int): RxBaseView(resId)

open class RxButton(resId: Int): RxView(resId)

open class RxTextView(resId: Int): RxView(resId) {
    private val writeText = MutableLiveData<String>()
    private val textColor = MutableLiveData<Int>()

    fun setText(text: String) {
        this.writeText.value = text
    }

    fun setTextColor(@ColorInt color: Int) {
        this.textColor.value = color
    }

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<TextView>(resId).run {
            this@RxTextView.writeText.observe(activity) {
                it?.let { text = it }
            }
            this@RxTextView.textColor.observe(activity) {
                it?.let { setTextColor(it) }
            }
        }
    }
}

open class RxEditText(resId: Int): RxTextView(resId) {
    private val readText = BehaviorSubject.createDefault(StringUtils.EMPTY)

    fun observeText(): Observable<String> = readText.hide()

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<EditText>(resId).run {
            addTextChangedListener {
                readText.onNext(it?.toString() ?: StringUtils.EMPTY)
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