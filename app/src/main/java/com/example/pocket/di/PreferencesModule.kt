package com.example.pocket.di

import com.example.pocket.data.preferences.PocketPreferencesManager
import com.example.pocket.data.preferences.PreferencesManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Binds
    @Singleton
    abstract fun bindPreferencesManager(
        pocketPreferencesManager: PocketPreferencesManager
    ): PreferencesManager
}