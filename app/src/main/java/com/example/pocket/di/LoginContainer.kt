package com.example.pocket.di

import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.utils.LoginViewModelFactory
import kotlinx.coroutines.Dispatchers

class LoginContainer {
    private val authenticationService = FirebaseAuthenticationService()
    private val defaultDispatcher = Dispatchers.IO
    val loginViewModel = LoginViewModelFactory(authenticationService, defaultDispatcher)
}