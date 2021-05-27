package com.example.pocket.data.preferences

import kotlinx.coroutines.flow.Flow


interface PreferencesManager {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateThemePreference(theme: Theme)


}
enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}

data class UserPreferences(val deviceTheme:Theme)