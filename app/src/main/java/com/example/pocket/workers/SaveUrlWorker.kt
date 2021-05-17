package com.example.pocket.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.network.PocketNetwork
import com.example.pocket.ui.activities.HandleUrlActivity

/**
 * This worker is triggered whenever the user saves the url
 * from another app.
 */
class SaveUrlWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork() =
        inputData.getString(HandleUrlActivity.EXTRA_URL)?.let {
            PocketRepository
                .getInstance(applicationContext)
                .saveUrl(
                    urlString = it,
                    thumbnail = PocketNetwork.getInstance().downloadImage(applicationContext, it)
                )
            Result.success()
        } ?: Result.failure()
}

