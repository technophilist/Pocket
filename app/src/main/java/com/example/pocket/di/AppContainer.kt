package com.example.pocket.di

import android.app.Application
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import kotlinx.coroutines.Dispatchers

class AppContainer(application: Application) {
    private val applicationContext = application.applicationContext

    //Default Dispatcher
    private val defaultRepositoryDispatcher = Dispatchers.IO
    private val defaultNetworkDispatcher = Dispatchers.IO

    private val network = PocketNetwork(applicationContext,Dispatchers.IO)
    private val database = UrlDatabase.getInstance(applicationContext)
    private val dao = database.getDao()


    val pocketRepository = PocketRepository(network, dao, defaultRepositoryDispatcher, applicationContext)
}