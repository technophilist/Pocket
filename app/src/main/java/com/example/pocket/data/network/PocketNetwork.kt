package com.example.pocket.data.network

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URL


interface Network{
    suspend fun fetchWebsiteContentTitle(url: String): String
    suspend fun downloadImage(context: Context, url: String): Drawable?
}

class PocketNetwork:Network {
    /**
     * Gets the content of the title tag of the [url]
     * @param url The string representation of the url
     * @return The content of the title tag of the website.
     */
    override suspend fun fetchWebsiteContentTitle(url: String): String =
        withContext(Dispatchers.IO) { Jsoup.connect(url).get().title() }

    /**
     * Tries to download the image from the src attribute of the image tag.
     * If it cannot , it will try to download the favicon of the website.If that
     * also fails , then it will return null.
     * @param context the context
     * @param url the complete url of the website
     * @return null if some error occurred while downloading
     */
    override suspend fun downloadImage(context: Context, url: String): Drawable? = withContext(Dispatchers.IO) {
        getImageUrl(url)?.let {
            Glide.with(context)
                .load(it)
                .submit()
                .get()
        } ?: downloadFavicon(context, url)
    }

    /**
     * Tries to get the url of the main image from the open graph meta tags in the html
     * document.If it cannot find the tag,it returns null.
     * @param url the complete url of the page
     * @return the url of the image
     */
    private suspend fun getImageUrl(url: String): String? = withContext(Dispatchers.IO) {
        val document = Jsoup.connect(url).get()
        val metaElements = document.select("meta")
        val openGraphElements = metaElements.filter { it.attr("property").contains("og:") }
        var imageUrl:String? = null
        openGraphElements.forEach{
            when(it.attr("property")){
                "og:image" -> { imageUrl = it.attr("content")}
            }
        }

        imageUrl
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
}