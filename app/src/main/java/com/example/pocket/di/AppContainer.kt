package com.example.pocket.di

import android.app.Application
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import com.example.pocket.data.preferences.PocketPreferencesManger
import kotlinx.coroutines.Dispatchers


private const val PREFERENCES_NAME = "pocket_user_preferences"
class AppContainer(application: Application) {
    private val applicationContext = application.applicationContext

    //default Dispatchers
    private val defaultRepositoryDispatcher = Dispatchers.IO
    private val defaultNetworkDispatcher = Dispatchers.IO

    private val network = PocketNetwork(applicationContext, defaultNetworkDispatcher)
    private val database = UrlDatabase.getInstance(applicationContext)
    private val dao = database.getDao()

    //preferences manager
    private val datastore = PreferenceDataStoreFactory.create{ applicationContext.preferencesDataStoreFile(PREFERENCES_NAME)}
    private val preferencesManager = PocketPreferencesManger(datastore)

    //dependencies
    val pocketRepository = PocketRepository(
        network,
        dao,
        preferencesManager,
        defaultRepositoryDispatcher,
        applicationContext
    )
}