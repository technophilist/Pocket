package com.example.pocket.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pocket.data.Repository
import com.example.pocket.viewmodels.HomeScreenViewModelImpl


class HomeScreenViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.AndroidViewModelFactory(application) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeScreenViewModelImpl(repository,application) as T
}