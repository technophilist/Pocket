package com.example.pocket.di

import android.app.Application
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import kotlinx.coroutines.Dispatchers

class AppContainer(application: Application) {
    private val applicationContext = application.applicationContext

    //default Dispatchers
    private val defaultRepositoryDispatcher = Dispatchers.IO
    private val defaultNetworkDispatcher = Dispatchers.IO

    private val network = PocketNetwork(applicationContext, defaultNetworkDispatcher)
    private val database = UrlDatabase.getInstance(applicationContext)
    private val dao = database.getDao()

    //dependencies
    val pocketRepository = PocketRepository(network, dao, defaultRepositoryDispatcher, applicationContext)
}