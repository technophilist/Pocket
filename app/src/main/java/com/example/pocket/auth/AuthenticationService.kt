package com.example.pocket.auth

import android.net.Uri

interface AuthenticationService {
    val isLoggedIn: Boolean
    suspend fun signIn(email: String, password: String): AuthenticationResult
    suspend fun createAccount(
        username: String,
        email: String,
        password: String,
        profilePhotoUri: Uri? = null
    ): AuthenticationResult

    suspend fun signOut()
}

sealed class AuthenticationResult {
    data class Success(val user: PocketUser) : AuthenticationResult()
    data class Failure(val exception: Exception) : AuthenticationResult()
}
