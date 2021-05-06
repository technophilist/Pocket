package com.example.pocket.utils

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


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



