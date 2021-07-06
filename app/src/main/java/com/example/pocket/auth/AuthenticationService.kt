package com.example.pocket.auth

import android.net.Uri

interface AuthenticationService {
    suspend fun signIn(email: String, password: String):AuthenticationResult
    suspend fun createAccount(
        username:String,
        email: String,
        password: String,
        profilePhotoUri: Uri?
    ):AuthenticationResult
}

sealed class AuthenticationResult {
    data class Success(val user: User) : AuthenticationResult()
    data class Failure(val exception: Exception) : AuthenticationResult()
}


