package com.example.pocket.di

import android.app.Application
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork
import kotlinx.coroutines.Dispatchers

class AppContainer(application: Application) {
    private val applicationContext = application.applicationContext
    private val network = PocketNetwork()
    private val database = UrlDatabase.getInstance(applicationContext)
    private val dao = database.getDao()
    private val defaultRepositoryDispatcher = Dispatchers.IO

    val pocketRepository = PocketRepository(network, dao, defaultRepositoryDispatcher, applicationContext)
}