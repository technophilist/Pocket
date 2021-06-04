package com.example.pocket.data.network

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.example.pocket.utils.getDownloadedResource
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
     * Gets the content of the title tag of the [url]
     * @param url the complete url of the website
     * @return The content of the title tag of the website.
     */
    override suspend fun fetchWebsiteContentTitle(url: URL): String =
        withContext(mDefaultDispatcher) { Jsoup.connect(url.toString()).get().title() }

    /**
     * Tries to download the image from the 'og:image' open graph and uses glide to get the
     * drawable.
     * @param url the complete url of the website
     * @return null if some error occurred while downloading
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
     * Tries to get the url of the main image from the open graph meta tags in the html
     * document.If it cannot find the tag,it returns null.
     * @param url the complete url of the page
     * @return the url of the image
     */
    private suspend fun getImageUrl(url: URL): String? = withContext(mDefaultDispatcher) {
        val document = Jsoup.connect(url.toString()).get()

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
     * Tries to download the favicon of a web page.If the favicon is not found or
     * glide throws an error , it will return null.
     * @param url the complete url of the web page
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

                //if it throws an error,try getting the favicon from the tags
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

                //return null if we are not able to find a tag with the favicon
            }
        }

    /**
     * Tries to get the favicon of the website using the "shortcut icon" or "icon"
     * attribute of the 'link' elements embedded in the webpage.
     * @param url the complete url of the website
     * @return returns null if it is not possible to find a link to the favicon
     * or the url string of the favicon if it is possible to get the link.
     */
    private suspend fun getFaviconUrlFromTags(url: URL): String? {
        return withContext(mDefaultDispatcher) {
            val document = Jsoup
                .connect(url.toString())
                .get()

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
