package com.example.pocket.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocket.data.Repository
import com.example.pocket.ui.activities.HandleUrlActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

/**
 * This worker is triggered whenever the user saves the url
 * from another app.
 */
class SaveUrlWorker(
    private val mContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(mContext, workerParameters) {

    override suspend fun doWork() =
        inputData.getString(HandleUrlActivity.EXTRA_URL)?.let {
            Repository
                .getInstance(mContext)
                .saveUrl(it, fetchWebsiteContentTitle(it), fetchImageUrl(it))
            Result.success()
        } ?: Result.failure()

    /**
     * Gets the content of the title tag of the [url]
     * @param url The string representation of the url
     * @return The content of the title tag of the website.
     */
    private suspend fun fetchWebsiteContentTitle(url: String) =
        withContext(Dispatchers.IO) { Jsoup.connect(url).get().title() }

    /**
     * Gets the value of "src" attr from the first instance of <img> tag in the website
     * @param url The string representation of the url
     * @return The value of the "src" attr of the image.If some error occurs,it
     *         returns null
     */
    private suspend fun fetchImageUrl(url: String): String? {
        return withContext(Dispatchers.IO) {
            //TODO("Download the correct thumbnail of each page")
            try {
                Jsoup.connect(url)
                    .get()
                    .select("img")
                    .first()
                    .attr("src")
            } catch (exception: Exception) {
                null
            }
        }
    }

}



