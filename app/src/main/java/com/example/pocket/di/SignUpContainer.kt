package com.example.pocket.di

import com.example.pocket.auth.AuthenticationService
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.utils.SignUpViewModelFactory
import kotlinx.coroutines.Dispatchers

class SignUpContainer(authenticationService: AuthenticationService) {
    private val defaultDispatcher  = Dispatchers.IO
    val signUpViewModelFactory = SignUpViewModelFactory(authenticationService,defaultDispatcher)
}