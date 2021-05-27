package com.example.pocket.data

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import com.example.pocket.data.PocketPreferencesManger.PreferenceKeys.DEVICE_THEME
import com.example.pocket.data.preferences.PreferencesManager
import com.example.pocket.data.preferences.Theme
import com.example.pocket.data.preferences.UserPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class PocketPreferencesManger(
    private val mDataStore: DataStore<Preferences>
) : PreferencesManager {

    private object PreferenceKeys {
        val DEVICE_THEME = stringPreferencesKey("device_theme_preference")
    }

    //TODO must handle errors
    override val userPreferences = mDataStore.data.map { preferences ->
        val deviceTheme = Theme.valueOf(preferences[DEVICE_THEME] ?: Theme.SYSTEM.name)
        UserPreferences(deviceTheme)
    }

    override suspend fun updateThemePreference(theme:Theme){
       mDataStore.edit {preferences->
           preferences[DEVICE_THEME] = theme.name
       }
    }
}

