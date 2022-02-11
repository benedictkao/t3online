package com.benkao.tictactoe.ui.base

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.ReplaySubject

interface RxViewFinder {

    /**
     * Method that returns a RxView type object that models a view's state.
     *
     * For RecyclerView, use getRecyclerView(Int, RecyclerView.Adapter,
     * RecyclerView.LayoutManager) instead
     */
    fun <T: RxView> getView(
        @IdRes id: Int,
        @ViewType viewType: Int = ViewType.VIEW
    ): T

    /**
     * Method that returns a RxRecyclerView object that models a recycler view's state.
     */
    fun <VH: RecyclerView.ViewHolder> getRecyclerView(
        @IdRes id: Int,
        adapter: RecyclerView.Adapter<VH>,
        layoutManager: RecyclerView.LayoutManager
    ): RxRecyclerView<VH>

    fun observeViews(): Observable<RxBaseView>
}

class RxViewFinderImpl: RxViewFinder {
    private val views = mutableMapOf<Int, RxBaseView>()
    private val viewsSubject = ReplaySubject.create<RxBaseView>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : RxView> getView(id: Int, viewType: Int): T {
        return views.getOrPut(id) {
            val view = when (viewType) {
                ViewType.TEXT_VIEW -> RxTextView(id)
                ViewType.IMAGE_VIEW -> RxImageView(id)
                else -> RxBaseView(id)
            }
            viewsSubject.onNext(view)
            view
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <VH : RecyclerView.ViewHolder> getRecyclerView(
        id: Int,
        adapter: RecyclerView.Adapter<VH>,
        layoutManager: RecyclerView.LayoutManager
    ): RxRecyclerView<VH> {
        return views.getOrPut(id) {
            RxRecyclerView(id, adapter, layoutManager)
        } as RxRecyclerView<VH>
    }

    override fun observeViews(): Observable<RxBaseView> = viewsSubject.hide()
}