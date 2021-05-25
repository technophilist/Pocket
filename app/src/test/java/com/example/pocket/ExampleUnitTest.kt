package com.example.pocket

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

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

    @Test
    fun imageFetchTest() {

        val document = Jsoup.connect("https://practice.geeksforgeeks.org/courses/dsa-self-paced?gclid=EAIaIQobChMI7Yrl64nk8AIV3oNLBR2MVQufEAAYASAAEgKadPD_BwE").get()
        val elements = document.select("meta")
        val ogElements = elements.filter { it.attr("property").contains("og:") }
        println("===================================================================================")
       ogElements.forEach{
           when(it.attr("property")){
               "og:title" -> { println("title ${it.attr("content")}")}
               "og:description" -> {println("desc ${it.attr("content")}")}
               "og:url" -> {println("url ${it.attr("content")}")}
               "og:image" -> {println("img ${it.attr("content")}")}
               "og:site_name"->{println("siteName ${it.attr("content")}")}
           }
       }
        println("===================================================================================")

    }



}