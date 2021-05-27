package com.example.pocket.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.pocket.data.preferences.PocketPreferencesManger.PreferenceKeys.DEVICE_THEME
import kotlinx.coroutines.flow.map


class PocketPreferencesManger(
    private val mDataStore: DataStore<Preferences>
) : PreferencesManager {

    private object PreferenceKeys {
        val DEVICE_THEME = stringPreferencesKey("device_theme_preference")
    }

    //TODO must handle errors
    override val userPreferences = mDataStore.data.map { preferences ->
        val deviceTheme = PocketPreferences.AppTheme.valueOf(preferences[DEVICE_THEME] ?: PocketPreferences.AppTheme.SYSTEM.name)
        UserPreferences(deviceTheme)
    }

    override suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme){
       mDataStore.edit {preferences->
           preferences[DEVICE_THEME] = appTheme.name
       }
    }
}

