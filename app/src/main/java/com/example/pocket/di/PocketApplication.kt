package com.example.pocket.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.pocket.BuildConfig
import com.example.pocket.workers.CleanUpDeletedItemsWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.time.Duration
import javax.inject.Inject

@HiltAndroidApp
class PocketApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        enqueueCleanUpWorker()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun enqueueCleanUpWorker() {
        val repeatInterval = Duration.ofHours(24)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<CleanUpDeletedItemsWorker>(repeatInterval)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CLEANUP_DELETED_ITEMS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    companion object {
        const val CLEANUP_DELETED_ITEMS_WORK_NAME =
            "com.example.pocket.di.PocketApplication.CLEANUP_DELETED_ITEMS_WORK_NAME"
    }
}