package com.example.pocket.utils

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


inline fun SearchView.doOnTextChanged(crossinline block: (newText: String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false
        override fun onQueryTextChange(newText: String): Boolean {
            block(newText)
            return true
        }
    })
}

enum class RecyclerViewSwipeDirections(val value: Int) {
    LEFT(ItemTouchHelper.LEFT),
    RIGHT(ItemTouchHelper.RIGHT),
    START(ItemTouchHelper.START),
    END(ItemTouchHelper.END)
}

inline fun RecyclerView.doOnItemSwiped(
    swipeDirection: RecyclerViewSwipeDirections,
    crossinline block: (viewHolder: RecyclerView.ViewHolder, direction: Int) -> Unit
) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = makeMovementFlags(0, swipeDirection.value)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(
            viewHolder: RecyclerView.ViewHolder,
            direction: Int
        ) = block(viewHolder, direction)
    }).attachToRecyclerView(this)

}

inline fun Snackbar.doOnDismissed(
    crossinline block: (transientBottomBar: Snackbar?, event: Int) -> Unit
): Snackbar {
    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onDismissed(
            transientBottomBar: Snackbar?,
            event: Int
        ) = block(transientBottomBar, event)

        override fun onShown(transientBottomBar: Snackbar?) = super.onShown(transientBottomBar)
    })
    return this
}



