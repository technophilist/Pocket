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
    suspend fun fetchThumbnail(url: URL): Drawable?
    suspend fun fetchFavicon(url: URL): Drawable?
}

class PocketNetwork @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoCoroutineDispatcher private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Network {

    /**
     * A hashmap that is used for memoizing the document object.
     *
     * Parsing an HTML document is resource intensive.By using
     * this Hashmap, if the [Document] object already exists for
     * a URL string(key of the map) in the map, then we can directly
     * fetch it from the map instead of fetching and parsing the same
     * HTML document again.
     * For example, both [fetchThumbnail] and [fetchFavicon] take an
     * instance of [URL] as parameter.
     * Without the presence of this hashmap, it becomes a necessity
     * to:
     * - Make a network request to get the HTML document.
     * - Parse the document.
     * - Perform the necessary operations.
     * These steps have to be performed for both [fetchFavicon] and
     * [fetchThumbnail] even if they are called subsequently with the
     * same url. Instead, both of these functions make use of this map.
     * So, a call to either one of these functions will memoize the
     * parsed document object. This precludes the need to fetch and
     * parse the document when the other function is called with the
     * same url.
     *
     * ```
     * val url = URL("www.example.com")
     * // fetches, parses, memoizes the document and fetches the thumbnail.
     * val thumbnail = network.fetchThumbnail(url)
     * // uses the memoized document object ineternally and fetches the favicon.
     * val favicon  = network.fetchFavicon(url)
     * ```
     */
    private val documentHashMap = HashMap<String, Document>()

    /**
     * Used to fetch the content title of the webpage using the [url].
     * If it is not possible to get the title,it returns an empty
     * string.
     */
    override suspend fun fetchWebsiteContentTitle(url: URL): String? =
        withContext(defaultDispatcher) {
            runCatching {
                documentHashMap
                    .getOrPut(url.toString()) { Jsoup.connect(url.toString()).getDocument() }
                    .title()
            }.getOrNull()
        }

    /**
     * Used to fetch the thumbnail image of the webpage as a drawable,
     * using the 'og:image' open graph tag with the help of glide.If any
     * exception is thrown,it will return null.
     * @param url The url of the web page
     */
    override suspend fun fetchThumbnail(url: URL): Drawable? =
        getImageUrl(url)?.let {
            withContext(defaultDispatcher) {
                runCatching {
                    Glide.with(context)
                        .asDrawable()
                        .load(it)
                        .getDownloadedResource()
                }.getOrNull()
            }
        }

    /**
     * Tries to get the url of the 'hero image' from the 'og:image'
     * open graph meta tags in the html document.If it cannot find the tag,
     * it will return null.
     * @param url the complete url of the page
     * @return the url of the image
     */
    private suspend fun getImageUrl(url: URL): String? = withContext(defaultDispatcher) {
        runCatching {
            documentHashMap
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
    override suspend fun fetchFavicon(url: URL): Drawable? = withContext(defaultDispatcher) {
        runCatching {
            //try getting the image using /favicon.ico convention used in the web
            Glide.with(context)
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
                Glide.with(context)
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
    private suspend fun getFaviconUrlFromTags(url: URL): String? = withContext(defaultDispatcher) {
        runCatching {
            documentHashMap
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

