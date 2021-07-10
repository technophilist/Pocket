package com.example.pocket.di

import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.utils.SignUpViewModelFactory
import kotlinx.coroutines.Dispatchers

class SignUpContainer {
    private val authenticationService = FirebaseAuthenticationService()
    private val defaultDispatcher  = Dispatchers.IO
    val signUpViewModelFactory = SignUpViewModelFactory(authenticationService,defaultDispatcher)
}