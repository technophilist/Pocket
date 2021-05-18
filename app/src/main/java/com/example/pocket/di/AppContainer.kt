package com.example.pocket.di

import android.app.Application
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.network.PocketNetwork

class AppContainer(application: Application) {
    private val applicationContext = application.applicationContext
    private val network = PocketNetwork()
    private val database = UrlDatabase.getInstance(applicationContext)
    private val dao = database.getDao()
    val pocketRepository = PocketRepository(network,dao,applicationContext)

}