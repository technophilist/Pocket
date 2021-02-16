package com.example.pocket.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class PocketNetwork private constructor() {
    /**
     * Gets the content of the title tag of the [url]
     * @param url The string representation of the url
     * @return The content of the title tag of the website.
     */
    suspend fun fetchWebsiteContentTitle(url: String): String =
        withContext(Dispatchers.IO) { Jsoup.connect(url).get().title() }

    /**
     * Gets the value of "src" attr from the first instance of <img> tag in the website
     * @param url The string representation of the url
     * @return The value of the "src" attr of the image.If some error occurs,it
     *         returns null
     */
    suspend fun fetchImageUrl(url: String): String? {
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

    companion object {
        private var mInstance: PocketNetwork? = null
        fun getInstance() = mInstance ?: synchronized(this) {
            mInstance = PocketNetwork()
            mInstance!!
        }
    }
}