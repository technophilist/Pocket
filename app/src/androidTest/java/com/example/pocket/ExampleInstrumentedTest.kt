package com.example.pocket

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bumptech.glide.Glide
import org.jsoup.Jsoup
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

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

    @Test
    fun downloadFaviconFromTagTest() {
        // <link rel="shortcut icon" href="URL goes here" />
        val document =
            Jsoup.connect("https://www.theverge.com/2021/5/27/22456248/oneplus-digital-wellpaper-app-usage-visualization-live-wallpaper")
                .get()
        //selecting all <link> elements
        val linkElements = document.select("link")
        val a = linkElements.filter {
            it.attr("rel") == "shortcut icon" || it.attr("rel") == "icon"
        }
        println(a.first().attr("href"))
    }

}

