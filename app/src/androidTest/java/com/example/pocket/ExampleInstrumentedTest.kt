package com.example.pocket

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bumptech.glide.Glide
import com.example.pocket.utils.downloadImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.net.URL

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var testContext: Context

//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.pocket", appContext.packageName)
//    }

    @Before
    fun setup() {
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun imageFetchTest() {
        val document = Jsoup.connect("https://www.theverge.com/platform/amp/2021/4/15/22386533/ios-app-scam-jungle-runner-magical-forest-apple-kosta").get()
        //use either img or amp-img
        val elements = document.getElementsByTag("amp-img")
        println(elements[0].attr("src"))
        assertNotNull(elements[0].attr("src"))

    }

    @Test
    fun faviconToDrawableTest() {
        val a = Glide.with(testContext)
            .load("https://www.apple.com/favicon.ico")
            .submit()
            .get()
        assertNotNull(a)
    }

   




}

