package com.benkao.tictactoe.ui.base

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.benkao.tictactoe.utils.StringUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

open class RxView(resId: Int): RxBaseView(resId)

open class RxButton(resId: Int): RxView(resId)

open class RxTextView(resId: Int): RxView(resId) {
    private val writeText = MutableLiveData<String>()

    fun setText(text: String) {
        this.writeText.value = text
    }

    override fun bind(activity: AppCompatActivity) {
        super.bind(activity)
        activity.findViewById<TextView>(resId).run {
            this@RxTextView.writeText.observe(activity) {
                it?.let { text = it }
            }
        }
    }
}

open class RxEditText(resId: Int): RxTextView(resId) {
    private val readText = BehaviorSubject.createDefault(StringUtils.EMPTY)

    fun observeText(): Observable<String> = readText.hide()

    override fun bind(activity: AppCompatActivity) {
        //TODO: TextWatcher not triggering

        super.bind(activity)
        activity.findViewById<EditText>(resId).run {
            addTextChangedListener {
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        println("before text changed")
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        println("on text changed")
                    }

                    override fun afterTextChanged(text: Editable?) {
                        println("after text changed")
                        readText.onNext(text?.toString() ?: StringUtils.EMPTY)
                    }
                }
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