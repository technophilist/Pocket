package com.example.pocket.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocket.data.Repository
import com.example.pocket.di.PocketApplication
import com.example.pocket.ui.activities.HandleUrlActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import java.net.URL
import javax.inject.Inject

/**
 * This worker is triggered whenever the user saves the url
 * from another app.
 */
@HiltWorker
class SaveUrlWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork() =
        inputData.getString(HandleUrlActivity.EXTRA_URL)?.let {
            repository.saveUrl(URL(it))
            Result.success()
        } ?: Result.failure()
}

