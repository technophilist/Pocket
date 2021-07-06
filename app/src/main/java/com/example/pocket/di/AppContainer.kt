package com.example.pocket.di

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import com.example.pocket.data.preferences.PocketPreferencesManager
import kotlinx.coroutines.Dispatchers


private const val PREFERENCES_NAME = "pocket_user_preferences"
private const val DATABASE_NAME = "Pocket_Database"

class AppContainer(application: Application) {

    private val applicationContext = application.applicationContext

    //default Dispatchers
    private val defaultRepositoryDispatcher = Dispatchers.IO
    private val defaultNetworkDispatcher = Dispatchers.IO

    //data
    private val network = PocketNetwork(applicationContext, defaultNetworkDispatcher)
    private val database = Room.databaseBuilder(
        applicationContext,
        UrlDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val dao = database.getDao()

    //preferences manager
    private val datastore = PreferenceDataStoreFactory.create {
        applicationContext.preferencesDataStoreFile(PREFERENCES_NAME)
    }
    private val preferencesManager = PocketPreferencesManager(datastore)

    //dependencies
    val pocketRepository = PocketRepository(
        network,
        dao,
        preferencesManager,
        defaultRepositoryDispatcher,
        applicationContext
    )
}