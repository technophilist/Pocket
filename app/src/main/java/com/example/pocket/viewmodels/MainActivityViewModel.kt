package com.example.pocket.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pocket.data.Repository
import com.example.pocket.data.preferences.PocketPreferences

interface MainActivityViewModel {
    val currentAppTheme: LiveData<PocketPreferences.AppTheme>
    fun changeAppTheme(newTheme: PocketPreferences.AppTheme)
}

class MainActivityViewModelImpl(
    private val mRepository: Repository
) : ViewModel(), MainActivityViewModel {

    override val currentAppTheme = mRepository.appTheme
    override fun changeAppTheme(newTheme: PocketPreferences.AppTheme) {
        mRepository.updateThemePreference(newTheme)
    }
}