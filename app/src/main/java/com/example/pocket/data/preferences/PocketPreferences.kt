package com.example.pocket.data.preferences

import kotlinx.coroutines.flow.Flow


interface PreferencesManager {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme)

}

interface PocketPreferences {
    enum class AppTheme {
        LIGHT,
        DARK,
        SYSTEM
    }
}

data class UserPreferences(val appTheme: PocketPreferences.AppTheme)