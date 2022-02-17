package com.benkao.tictactoe.ui.base

import android.os.Parcelable
import kotlin.reflect.KClass

class ActivityIntent private constructor(
    val clazz: KClass<*>?,
    val data: Pair<String,Parcelable>?,
    val flags: Int?
){
    class Builder {
        private var clazz: KClass<*>? = null
        private var flags: Int? = null
        private var data: Pair<String,Parcelable>? = null

        fun clazz(clazz: KClass<*>): Builder {
            this.clazz = clazz
            return this
        }

        fun data(key: String, data: Parcelable): Builder {
            this.data = Pair(key, data)
            return this
        }

        fun flags(flags: Int): Builder {
            this.flags = flags
            return this
        }

        fun build(): ActivityIntent =
            ActivityIntent(
                clazz,
                data,
                flags
            )
    }
}