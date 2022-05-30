package com.example.pocket.di

import android.app.Application
import androidx.room.Room
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import com.example.pocket.data.preferences.PreferencesManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers


private const val PREFERENCES_NAME = "pocket_user_preferences"
private const val DATABASE_NAME = "Pocket_Database"

class AppContainer(application: Application) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppContainerEntryPoint {
        fun getPreferencesManager(): PreferencesManager
    }

    private val applicationContext = application.applicationContext

    // default Dispatchers
    private val defaultRepositoryDispatcher = Dispatchers.IO
    private val defaultNetworkDispatcher = Dispatchers.IO

    // data
    private val network = PocketNetwork(applicationContext, defaultNetworkDispatcher)
    private val database = Room.databaseBuilder(
        applicationContext,
        UrlDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val dao = database.getDao()

    private val preferencesManager = getPreferencesManager()

    // dependencies
    val pocketRepository = PocketRepository(
        network,
        dao,
        preferencesManager,
        defaultRepositoryDispatcher,
        applicationContext
    )

    val authenticationService = FirebaseAuthenticationService()
    private fun getPreferencesManager(): PreferencesManager = EntryPointAccessors.fromApplication(
        this.applicationContext,
        AppContainerEntryPoint::class.java
    ).getPreferencesManager()
}