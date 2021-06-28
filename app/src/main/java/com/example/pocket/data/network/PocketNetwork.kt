package com.example.pocket.data.network

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.example.pocket.utils.getDownloadedResource
import com.example.pocket.utils.getDocument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL
import java.util.*


interface Network {
    suspend fun fetchWebsiteContentTitle(url: URL): String
    suspend fun downloadImage(url: URL): Drawable?
    suspend fun downloadFavicon(url: URL): Drawable?
}

class PocketNetwork(
    private val mContext: Context,
    private val mDefaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Network {

    /**
     * Gets the content title of the webpage using the [url].
     */
    override suspend fun fetchWebsiteContentTitle(url: URL): String =
        withContext(mDefaultDispatcher) {
            //ERROR HANDLING!!!
            Jsoup.connect(url.toString()).getDocument().title()
        }

    /**
     * Used to download the 'hero' image of the webpage as a drawable,
     * using the 'og:image' open graph tag with the help of glide.If any
     * exception is thrown,it will return null.
     * @param url The url of the web page
     */
    override suspend fun downloadImage(url: URL): Drawable? =
        getImageUrl(url)?.let {
            runCatching {
                Glide.with(mContext)
                    .asDrawable()
                    .load(it)
                    .getDownloadedResource()
            }.getOrNull()
        }

    /**
     * Tries to get the url of the 'hero image' from the 'og:image'
     * open graph meta tags in the html document.If it cannot find the tag,
     * it will return null.
     * @param url the complete url of the page
     * @return the url of the image
     */
    private suspend fun getImageUrl(url: URL): String? = withContext(mDefaultDispatcher) {
        val document = Jsoup.connect(url.toString()).getDocument()

        //selecting all the meta elements
        val metaElements = document.select("meta")

        //selecting all meta graph tags
        val openGraphElements = metaElements.filter { it.attr("property").contains("og:") }

        //selecting the 'og:image' tag
        val ogImageTag = openGraphElements.find { it.attr("property") == "og:image" }

        //returning the value of the 'content' property which contains the url of the image
        ogImageTag?.attr("content")
    }

    /**
     * Tries to download the favicon of a web page using the [url].If the favicon is
     * not found or glide throws an error , it will return null.
     */
    override suspend fun downloadFavicon(url: URL): Drawable? =
        withContext(mDefaultDispatcher) {
            runCatching {
                //try getting the image using /favicon.ico convention used in the web
                Glide.with(mContext)
                    .asDrawable()
                    .load("${url.protocol}://${url.host}/favicon.ico")
                    .getDownloadedResource()
            }.getOrElse {

                /*
                 * If it throws an error,try getting the favicon from the tags.
                 * If it is still not possible to get the favicon using the tags,
                 * return null.
                 */
                getFaviconUrlFromTags(url)?.let { urlString ->
                    /*
                    we are not using a try/catch block because the
                    [getFaviconUrlFromTags] returns a url string only if
                    it can find a valid url to the favicon
                     */
                    Glide.with(mContext)
                        .asDrawable()
                        .load(urlString)
                        .getDownloadedResource()
                }
            }
        }

    /**
     * Tries to get the favicon of the website using the "shortcut icon" or "icon"
     * attribute of the 'link' elements embedded in the webpage using the [url].If it
     * is not possible to find a link to the favicon, it returns null else it returns
     * the string representing the url of the favicon.
     */
    private suspend fun getFaviconUrlFromTags(url: URL): String? {
        return withContext(mDefaultDispatcher) {
            val document = Jsoup
                .connect(url.toString())
                .getDocument()

            //selecting all <link> elements
            val linkElements = document.select("link")

            //filtering all the link elements that have icon/shortcut icon as their attribute
            val shortcutElements = linkElements.filter {
                it.attr("rel") == "shortcut icon" || it.attr("rel") == "icon"
            }

            try {
                //selecting the first element and getting the url of the favicon
                shortcutElements.first().attr("href")

            } catch (exception: NoSuchElementException) {

                //if it throws a NoSuchElementException , it means that the list is empty
                //returning null since the list is empty
                null
            }
        }
    }

}
