package com.example.pocket.data.network

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.net.URL

class PocketNetworkTest{
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val network: Network = PocketNetwork(context)

    @Test
    fun imageFetchTest() {
        val urlContainingImage = "https://9to5mac.com/2021/07/23/exclusive-apple-testing-new-external-display-with-a-dedicated-a13-chip-and-neural-engine/"
        val urlNotContainingImage = "https://www.google.com"
        runBlocking {
            // try fetching the image from a website containing an image
            val image = network.fetchImage(URL(urlContainingImage))
            assertNotNull(image)
        }
        runBlocking{
            // try fetching the image from a website not containing an image
            val image = network.fetchImage(URL(urlNotContainingImage))
            assertNull(image)
        }
    }

    @Test
    fun faviconFetchFaviconTest() {
        val urlContainingFavicon = "https://www.theverge.com/22586815/oneplus-nord-2-5g-review-specs-price-camera-screen"
        val urlNotContainingFavicon = "https://www.theverge.com/favicon.ico"

        runBlocking {
            // try fetching the favicon from a website that has a favicon
            val favicon = network.fetchFavicon(URL(urlContainingFavicon))
            assertNotNull(favicon)
        }

        runBlocking {
            // try fetching the favicon from a website that does not have a favicon
            val favicon = network.fetchFavicon(URL(urlNotContainingFavicon))
            assertNull(favicon)
        }
    }
}