package com.example.pocket.data.network

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.example.pocket.di.IoCoroutineDispatcher
import com.example.pocket.utils.getDocument
import com.example.pocket.utils.getDownloadedResource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import javax.inject.Inject


interface Network {
    suspend fun fetchWebsiteContentTitle(url: URL): String?
    suspend fun fetchImage(url: URL): Drawable?
    suspend fun fetchFavicon(url: URL): Drawable?
}

class PocketNetwork @Inject constructor(
    @ApplicationContext private val mContext: Context,
    @IoCoroutineDispatcher private val mDefaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Network {

    /**
     * A hashmap that is used for memoizing the document object.
     *
     * Parsing an HTML document is resource intensive.By using
     * this Hashmap, if the [Document] object already exists for
     * a URL string(key of the map) in the map, then we can directly
     * fetch it from the map instead of parsing the same HTML document
     * again.
     */
    private val mDocumentHashMap = HashMap<String, Document>()

    /**
     * Used to fetch the content title of the webpage using the [url].
     * If it is not possible to get the title,it returns an empty
     * string.
     */
    override suspend fun fetchWebsiteContentTitle(url: URL): String? =
        withContext(mDefaultDispatcher) {
            runCatching {
                mDocumentHashMap
                    .getOrPut(url.toString()) { Jsoup.connect(url.toString()).getDocument() }
                    .title()
            }.getOrNull()
        }

    /**
     * Used to fetch the 'hero' image of the webpage as a drawable,
     * using the 'og:image' open graph tag with the help of glide.If any
     * exception is thrown,it will return null.
     * @param url The url of the web page
     */
    override suspend fun fetchImage(url: URL): Drawable? =
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
        runCatching {
            mDocumentHashMap
                .getOrPut(url.toString()) { Jsoup.connect(url.toString()).getDocument() }
                .select("meta")
                .filter { metaElement -> metaElement.attr("property").contains("og:") }
                .find { openGraphElement -> openGraphElement.attr("property") == "og:image" }
                ?.attr("content") // the value of the 'content' attribute contains the url of the image
        }.getOrNull()
    }

    /**
     * Tries to fetch the favicon of a web page as a Drawable using the [url].
     * If the favicon is not found or glide throws an error , it will return null.
     */
    override suspend fun fetchFavicon(url: URL): Drawable? = withContext(mDefaultDispatcher) {
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
    private suspend fun getFaviconUrlFromTags(url: URL): String? = withContext(mDefaultDispatcher) {
        runCatching {
            mDocumentHashMap
                .getOrPut(url.toString()) { Jsoup.connect(url.toString()).getDocument() }
                .select("link")
                .firstOrNull { linkElement ->
                    linkElement.attr("rel") == "shortcut icon" || linkElement.attr(
                        "rel"
                    ) == "icon"
                }
                ?.attr("href")
        }.getOrNull()
    }
}

