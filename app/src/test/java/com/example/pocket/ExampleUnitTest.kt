package com.example.pocket

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.junit.Assert.*
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
    fun runCatchingTest() {
        val a = kotlin.runCatching { "test" }.getOrNull()
        println(a)
        assertNotNull(a)
        val b = kotlin.runCatching { 2/0 }.getOrNull()
        assertNotNull(b)
    }



}