package com.example.pocket.di

import android.app.Application

class PocketApplication:Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }

}