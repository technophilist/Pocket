package com.example.pocket.data.network

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL

@RunWith(AndroidJUnit4::class)
class PocketNetworkTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val network: Network = PocketNetwork(context)

    @Test
    fun imageFetchTest_invalidURLs_returnsNull() {
        // given a list of URL's that doesn't contain an image in them
        val urlList = listOf(
            URL("https://www.google.com"),
            URL("https://www.quora.com/"),
            URL("https://twitter.com/") // url without og:image tag
        )
        urlList.forEach { url ->
            val drawable = runBlocking {
                // when calling fetchFavicon()
                network.fetchImage(url)
            }
            // the drawable must be null
            assertNull(drawable)
        }
    }

    @Test
    fun imageFetchTest_validURLs_returnsNotNull() {
        // given a list of URL's that contain an image in them
        val urlList = listOf(
            URL("https://9to5google.com/2021/07/28/android-studio-arctic-fox-wear-os-pairing-jetpack-compose"),
            URL("https://9to5mac.com/2021/07/23/exclusive-apple-testing-new-external-display-with-a-dedicated-a13-chip-and-neural-engine/"),
            URL("https://www.xda-developers.com/android-studio-arctic-fox-jetpack-compose-1-0/"),
            URL("https://www.cnbc.com/2021/07/27/apples-iphone-hot-streak-will-run-into-global-chip-shortage.html"),
            URL("https://developer.android.com/studio?gclid=EAIaIQobChMImZmDq6uG8gIVjx0rCh3QYANqEAAYASAAEgJD9vD_BwE&gclsrc=aw.ds")
        )
        urlList.forEach { url ->
            val drawable = runBlocking {
                // when calling fetchFavicon()
                network.fetchFavicon(url)
            }
            // the drawable must not be null
            assertNotNull(drawable)
        }
    }

    @Test
    fun faviconFetchTest_validURLs_returnsNotNull() {
        // given a list of valid URL's that have a favicon
        val urlList = listOf(
            URL("https://www.google.com"),
            URL("https://www.theverge.com"),// this url doesn't have the favicon at the usual hostname/favicon.ico
            URL("https://twitter.com/?lang=en")
        )
        urlList.forEach { url ->
            val drawable = runBlocking {
                // when calling fetchFavicon()
                network.fetchFavicon(url)
            }
            // the drawable must not be null
            assertNotNull(drawable)
        }
    }

    @Test
    fun contentTitleFetchTest_validURLs_returnsNotNull() {
        // given a list of valid URL's that have a content title
        val urlList = listOf(
            URL("https://www.theverge.com/2021/7/31/22603429/rick-astley-never-gonna-give-you-up-1-billion-youtube"),
            URL("https://www.wsj.com/video/series/inside-tiktoks-highly-secretive-algorithm/investigation-how-tiktok-algorithm-figures-out-your-deepest-desires/6C0C2040-FF25-4827-8528-2BD6612E3796"),
            URL("https://9to5mac.com/2021/07/31/apple-boots-tinder-for-anti-vaxxers-app-from-the-app-store-for-violating-covid-19-guidelines/"),
            URL("https://www.apple.com/newsroom/2021/07/educators-in-australia-embrace-swift-to-forge-a-new-future/"),
        )
        urlList.forEach { url ->
            val contentTitle = runBlocking {
                // when calling fetchWebsiteContentTitle()
                network.fetchWebsiteContentTitle(url)
            }
            // the content title must not be null
            assertNotNull(contentTitle)
        }
    }

}