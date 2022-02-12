package com.benkao.tictactoe.ui.base

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.ReplaySubject
import kotlin.ClassCastException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface RxViewFinder {

    /**
     * Method that returns a RxView type object that models a view's state.
     *
     * For RecyclerView, use getRecyclerView(Int, RecyclerView.Adapter,
     * RecyclerView.LayoutManager) instead
     */
    fun getRxView(@IdRes id: Int): Single<RxView>

    /**
     * Method that returns a RxView type object that models a view's state.
     *
     * For RecyclerView, use getRecyclerView(Int, RecyclerView.Adapter,
     * RecyclerView.LayoutManager) instead
     */
    fun <T: RxView> getRxView(
        @IdRes id: Int,
        clazz: KClass<T>
    ): Single<T>

    /**
     * Method that returns a RxRecyclerView object that models a recycler view's state.
     */
    fun <VH: RecyclerView.ViewHolder> getRxRecyclerView(
        @IdRes id: Int,
        adapter: RecyclerView.Adapter<VH>,
        layoutManager: RecyclerView.LayoutManager
    ): Single<RxRecyclerView<*>>

    /**
     * Provides a stream of RxBaseView type objects. Replays the stream on subscribe.
     */
    fun observeViews(): Observable<RxBaseView>
}

class RxViewFinderImpl: RxViewFinder {
    private val views = mutableMapOf<Int, RxBaseView>()
    private val viewsSubject = ReplaySubject.create<RxBaseView>()

    override fun getRxView(id: Int): Single<RxView> {
        val view = views.getOrPut(id) {
            RxView(id)
        } as RxView
        return Single.just(view)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : RxView> getRxView(
        id: Int,
        clazz: KClass<T>
    ): Single<T> {
        return try {
            val view = views.getOrPut(id) {
                clazz.primaryConstructor!!.call(id)
                    .apply { viewsSubject.onNext(this) }
            } as T
            Single.just(view)
        } catch (e: ClassCastException) {
            Single.error(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <VH : RecyclerView.ViewHolder> getRxRecyclerView(
        id: Int,
        adapter: RecyclerView.Adapter<VH>,
        layoutManager: RecyclerView.LayoutManager
    ): Single<RxRecyclerView<*>> {
        val recyclerView = views.getOrPut(id) {
            RxRecyclerView(id, adapter, layoutManager)
        }
        return if (recyclerView is RxRecyclerView<*>) Single.just(recyclerView)
        else Single.error(ClassCastException())
    }

    override fun observeViews(): Observable<RxBaseView> = viewsSubject.hide()
}