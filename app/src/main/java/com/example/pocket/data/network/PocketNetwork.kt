package com.example.pocket.data.network

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URL

class PocketNetwork private constructor() {
    /**
     * Gets the content of the title tag of the [url]
     * @param url The string representation of the url
     * @return The content of the title tag of the website.
     */
    suspend fun fetchWebsiteContentTitle(url: String): String =
        withContext(Dispatchers.IO) { Jsoup.connect(url).get().title() }

    /**
     * Tries to download the image from the src attribute of the image tag.
     * If it cannot , it will try to download the favicon of the website.If that
     * also fails , then it will return null.
     * @param context the context
     * @param url the complete url of the website
     * @return null if some error occurred while downloading
     */
    suspend fun downloadImage(context: Context, url: String): Drawable? = withContext(Dispatchers.IO) {
        getImageUrl(url)?.let {
            Glide.with(context)
                .load(it)
                .submit()
                .get()
        } ?: downloadFavicon(context, url)
    }

    /**
     * Tries to get the url of the first image that is displayed in the web page.It tries to
     * get the src from the html img tag.If it fails to find the tag , it tries to get the src
     * from the amp-img tag , which is commonly used in the web to display images.If it cannot
     * find both the tags it returns null.
     * @param url the complete url of the page
     * @return the url of the image
     */
    private suspend fun getImageUrl(url: String): String? = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()
        var imgTags = document.getElementsByTag("img")
        if (imgTags.isEmpty()) {
            imgTags = document.getElementsByTag("amp-img")
        }
        if (imgTags.isEmpty()) null
        else imgTags[0].absUrl("src")
    }

    /**
     * Tries to download the favicon of a web page.If the favicon is not found or
     * glide throws an error , it will return null.
     * @param urlString the complete url of the web page
     */
    private suspend fun downloadFavicon(context: Context, urlString: String): Drawable? =
        withContext(Dispatchers.IO) {
            val url = URL(urlString)
            try {
                Glide.with(context)
                    .load("${url.protocol}://${url.host}/favicon.ico")
                    .submit()
                    .get()
            } catch (exception: Exception) {
                null
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