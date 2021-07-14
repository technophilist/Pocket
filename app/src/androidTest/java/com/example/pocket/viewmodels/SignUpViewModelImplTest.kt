package com.example.pocket.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.pocket.auth.AuthServiceInvalidEmailException
import com.example.pocket.auth.AuthServiceInvalidPasswordException
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.auth.FirebaseAuthenticationService
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SignUpViewModelImplTest{

    // Run all architecture components synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val authService = FirebaseAuthenticationService()
    private val viewmodel = SignUpViewModelImpl(authService)

    /**
     * Tests whether the viewmodel correctly identifies a list of invalid emails
     * as invalid.
     */
    @Test
    fun invalidEmailValidationTest() {
        // a list of invalid emails
        val invalidEmails = listOf(
            "test@t",
            " ",
            "",
            "t",
            ".com",
            "www.google.com",
            "test@Test@test.com",
            " @test.com"
        )

        val observer = Observer<AuthenticationResult> {
            // assert that the value would be an instance of AuthServiceInvalidEmailException
            assertTrue(it is AuthenticationResult.Failure && it.authServiceException is AuthServiceInvalidEmailException)
        }
        // observe the live data
        viewmodel.accountCreationResult.observeForever(observer)
        invalidEmails.forEach { viewmodel.createNewAccount("test", it, "Ad12345678") }
        // remove observer
        viewmodel.accountCreationResult.removeObserver(observer)
    }

    /**
     * Tests whether the viewmodel correctly identifies a list of invalid passwords
     * as invalid.
     */
    @Test
    fun accountCreationTest(){
        // a list of invalid passwords
        val invalidPasswords = listOf(
            "123456",
            "1Aa",
            "abcdefghijkl",
            "ABCDEFGHIJKL",
            "",
            " ",
            "        "// 8 whitespace characters
        )
        val observer = Observer<AuthenticationResult>{
            // assert that the value would be an instance of AuthServiceInvalidPasswordException
            assertTrue(it is AuthenticationResult.Failure && it.authServiceException is AuthServiceInvalidPasswordException)
        }
        // observe livedata
        viewmodel.accountCreationResult.observeForever(observer)
        invalidPasswords.forEach { viewmodel.createNewAccount("test", "test@test,com", it) }
        // remove observers
        viewmodel.accountCreationResult.removeObserver(observer)
    }

}