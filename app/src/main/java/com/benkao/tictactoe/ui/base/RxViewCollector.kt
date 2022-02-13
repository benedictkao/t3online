package com.benkao.tictactoe.ui.base

import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface RxViewStream {

    /**
     * Provides a stream of RxBaseView type objects.
     */
    fun observeStream(): Observable<RxBaseView>
}

class RxViewCollector: RxViewStream {
    private val views = ConcurrentHashMap<Int, Pair<KClass<*>, RxBaseView>>()
    private val viewsSubject = ReplaySubject.create<RxBaseView>()

    /**
     * Method that puts a RxView type object into the view finder.
     *
     * For RxRecyclerView, use putRxRecyclerView(Int, RecyclerView.Adapter,
     * RecyclerView.LayoutManager) instead
     */
    fun <T : RxView> addView(id: Int, clazz: KClass<T>): Single<T> {
        clazz.primaryConstructor!!.call(id).let {
            viewsSubject.onNext(it)
            views[id] = Pair(clazz, it)
            return Single.just(it)
        }
    }

    /**
     * Method that puts a RxRecyclerView object into the view finder.
     */
    fun <VH : RecyclerView.ViewHolder> addRecyclerView(
        id: Int,
        adapter: RecyclerView.Adapter<VH>,
        layoutManager: RecyclerView.LayoutManager
    ): Single<RxRecyclerView<VH>> {
        RxRecyclerView(id, adapter, layoutManager).let {
            viewsSubject.onNext(it)
            views[id] = Pair(RxRecyclerView::class, it)
            return Single.just(it)
        }
    }

    override fun observeStream(): Observable<RxBaseView> = viewsSubject.hide()
}