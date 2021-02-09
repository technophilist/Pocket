package com.example.pocket.utils

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


inline fun SearchView.doOnTextChanged(crossinline block: (newText: String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false
        override fun onQueryTextChange(newText: String): Boolean {
            block(newText)
            return true
        }
    })
}

inline fun RecyclerView.doOnItemSwiped(
    crossinline block: (viewHolder: RecyclerView.ViewHolder, direction: Int) -> Unit
) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = makeMovementFlags(0, ItemTouchHelper.LEFT)

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