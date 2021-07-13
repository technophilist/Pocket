package com.example.pocket.di

import com.example.pocket.auth.AuthenticationService
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.utils.LoginViewModelFactory
import kotlinx.coroutines.Dispatchers

class LoginContainer(authenticationService:AuthenticationService) {
    private val defaultDispatcher = Dispatchers.IO
    val loginViewModelFactory = LoginViewModelFactory(authenticationService, defaultDispatcher)
}