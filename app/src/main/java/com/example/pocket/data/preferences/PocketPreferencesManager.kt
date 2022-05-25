package com.example.pocket.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pocket.data.preferences.PocketPreferencesManager.PreferenceKeys.DEVICE_THEME
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class PocketPreferencesManager @Inject constructor(
    private val mDataStore: DataStore<Preferences>
) : PreferencesManager {

    private object PreferenceKeys {
        val DEVICE_THEME = stringPreferencesKey("device_theme_preference")
    }
    
    override val userPreferences = mDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                /*
                if an IOException occurs while reading data,return a User preferences
                object with default value
                */
                UserPreferences(PocketPreferences.AppTheme.SYSTEM)
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val deviceTheme = PocketPreferences.AppTheme.valueOf(
                preferences[DEVICE_THEME] ?: PocketPreferences.AppTheme.SYSTEM.name
            )
            UserPreferences(deviceTheme)
        }

    override suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme) {
        mDataStore.edit { preferences ->
            preferences[DEVICE_THEME] = appTheme.name
        }
    }
}

