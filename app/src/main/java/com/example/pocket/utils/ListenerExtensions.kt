package com.example.pocket.utils

import androidx.appcompat.widget.SearchView


fun SearchView.doOnTextChanged(block: (newText: String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false
        override fun onQueryTextChange(newText: String): Boolean {
            block(newText)
            return true;
        }
    })
}