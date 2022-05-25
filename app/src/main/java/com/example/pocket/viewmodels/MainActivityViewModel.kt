package com.example.pocket.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pocket.data.Repository
import com.example.pocket.data.preferences.PocketPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

interface MainActivityViewModel {
    val currentAppTheme: LiveData<PocketPreferences.AppTheme>
    fun changeAppTheme(newTheme: PocketPreferences.AppTheme)
}

@HiltViewModel
class MainActivityViewModelImpl @Inject constructor(
    private val mRepository: Repository
) : ViewModel(), MainActivityViewModel {

    override val currentAppTheme = mRepository.appTheme
    override fun changeAppTheme(newTheme: PocketPreferences.AppTheme) {
        mRepository.updateThemePreference(newTheme)
    }

    override fun onCleared() {
        Timber.d("Cleard")
        super.onCleared()
    }
}