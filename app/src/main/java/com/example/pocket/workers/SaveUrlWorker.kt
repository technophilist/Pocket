package com.example.pocket.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pocket.data.database.Repository
import com.example.pocket.ui.activities.HandleUrlActivity
import com.example.pocket.utils.downloadImage

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
            Repository
                .getInstance(applicationContext)
                .saveUrl(it,downloadImage(applicationContext,it))
            Result.success()
        } ?: Result.failure()
}

