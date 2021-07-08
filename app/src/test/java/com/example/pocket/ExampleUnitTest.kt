package com.example.pocket

import android.util.Patterns
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.utils.containsDigit
import com.example.pocket.utils.containsLowercase
import com.example.pocket.utils.containsUppercase
import com.example.pocket.viewmodels.LoginViewModelImpl
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
//        val a = kotlin.runCatching { "test" }.getOrNull()
//        println(a)
//        assertNotNull(a)
//        val b = kotlin.runCatching { 2/0 }.getOrNull()
//        assertNotNull(b)
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

    @Test
    fun firebaseTest(){
        val authService = FirebaseAuthenticationService()
        runBlocking {
            authService.signIn("test@test.com","testpassword")
        }

    }

    @Test
    fun stringUtilsTest(){
        val testString1 = "paSsword"
        val testSting2 = "PASSwORD"
        val testString3 = "Pa3sword"

        assertTrue(testString1.containsUppercase())
        assertTrue(testSting2.containsLowercase())
        assertTrue(testString3.containsDigit())

        assertFalse(!testString1.containsUppercase())
        assertFalse(!testString1.containsUppercase())
        assertFalse(!testString1.containsDigit())

    }

    @Test
    fun emailValidationTest(){
        val testEmails = listOf("test@test.com","test@t"," ","","t",".com","www.google.com")
        testEmails.forEach{
            assertFalse(it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it).matches())
        }
        val trueEmail = "test@test.com"

        assertTrue(trueEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trueEmail).matches())

    }


}