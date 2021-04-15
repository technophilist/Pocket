package com.example.pocket.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

inline fun Fragment.onBackPressed(crossinline block: OnBackPressedCallback.() -> Unit) {
    activity?.let { it.onBackPressedDispatcher.addCallback(viewLifecycleOwner) { block() } }
}