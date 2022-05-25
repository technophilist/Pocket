package com.example.pocket.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.pocket.data.preferences.PocketPreferencesManager
import com.example.pocket.data.preferences.PreferencesManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFERENCES_NAME = "pocket_user_preferences"

@InstallIn(SingletonComponent::class)
@Module
object DatastoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create {
        applicationContext.preferencesDataStoreFile(PREFERENCES_NAME)
    }
}