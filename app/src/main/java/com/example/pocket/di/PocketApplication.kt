package com.example.pocket.di

import android.app.Application
import com.example.pocket.BuildConfig
import timber.log.Timber

class PocketApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        appContainer = AppContainer(this)
    }

}