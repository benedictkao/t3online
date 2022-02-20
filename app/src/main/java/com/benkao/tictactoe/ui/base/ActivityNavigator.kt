package com.benkao.tictactoe.ui.base

import android.os.Parcelable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlin.reflect.KClass

interface ActivityNavigator {

    fun plan(clazz: KClass<*>): ActivityPlanner

    fun finish()

    fun observePlans(): Observable<ActivityPlan>
}

class ActivityNavigatorImpl: ActivityNavigator {

    private val planSubject = BehaviorSubject.create<ActivityPlan>()

    override fun plan(clazz: KClass<*>): ActivityPlanner = ActivityPlanner(clazz, planSubject)

    override fun finish() {
        planSubject.onNext(ActivityPlan.FINISH)
    }

    override fun observePlans(): Observable<ActivityPlan> = planSubject.hide()
}

class ActivityPlanner(
    private val clazz: KClass<*>,
    private val planSubject: Subject<ActivityPlan>,
) {

    private var flags: Int? = null
    private var data: Pair<String,Parcelable>? = null

    fun setFlags(flags: Int): ActivityPlanner {
        this.flags = flags
        return this
    }

    fun setData(key: String, data: Parcelable): ActivityPlanner {
        this.data = Pair(key, data)
        return this
    }

    fun start(finishCurrent: Boolean) {
        planSubject.onNext(ActivityPlan(clazz, flags, data, finishCurrent))
    }
}

data class ActivityPlan(
    val clazz: KClass<*>?,
    val flags: Int?,
    val data: Pair<String,Parcelable>?,
    val finishCurrent: Boolean
) {
    companion object {
        val FINISH = ActivityPlan(null, null, null, true)
    }
}