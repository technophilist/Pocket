package com.example.pocket

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun imageFetchTest() {
        val arr = Jsoup.connect("https://www.theverge.com/platform/amp/2021/4/15/22386533/ios-app-scam-jungle-runner-magical-forest-apple-kosta")
            .get()
            .allElements

        for (e in arr){
            println(e)
        }


       assert(arr.size>0)

    }

}