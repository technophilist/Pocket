package com.example.pocket

import android.content.Context
import android.util.Patterns
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bumptech.glide.Glide
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.di.PocketApplication
import com.example.pocket.di.SignUpContainer
import com.example.pocket.viewmodels.SignUpViewModelImpl
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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
        val document =
            Jsoup.connect("https://www.theverge.com/platform/amp/2021/4/15/22386533/ios-app-scam-jungle-runner-magical-forest-apple-kosta")
                .get()
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

    @Test
    fun firebaseTest() {
        val authService = FirebaseAuthenticationService()

        //signing in
        runBlocking {
            when (val authSer = authService.createAccount("t","1", "testtest")) {
                is AuthenticationResult.Failure -> println(authSer.authServiceException)
                is AuthenticationResult.Success -> println(authSer.user)
            }
        }

        //test whether the user is logged in after a successful log-in method call
        assertTrue(authService.isLoggedIn)

        //sign out
        runBlocking {
            authService.signOut()
        }

        //est whether the user is logged in after a successful log-out method call
        assertFalse(authService.isLoggedIn)

    }

    @Test
    fun emailValidationTest(){
        val testEmails = listOf("test@t"," ","","t",".com","www.google.com")
        testEmails.forEach{
            assertFalse(it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it).matches())
        }
        val trueEmail = "test@test.com"

        assertTrue(trueEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trueEmail).matches())

    }

    @Test
    fun accountCreationTest(){
        val appContainer = (testContext.applicationContext as PocketApplication).appContainer
        appContainer.signUpContainer = SignUpContainer()

        val viewModel = appContainer.signUpContainer!!.signUpViewModelFactory.create(SignUpViewModelImpl::class.java)
        viewModel.createNewAccount("FirstName Second Name","email@email.com","1234567890")

    }

}

