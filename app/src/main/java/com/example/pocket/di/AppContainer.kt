package com.example.pocket.di

import android.app.Application
import com.example.pocket.data.PocketRepository

class AppContainer(application: Application) {
    
    val pocketRepository = PocketRepository(application)

}