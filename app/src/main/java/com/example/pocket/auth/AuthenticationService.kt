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

enum class AuthenticationServiceException(
    val errorMessage: String? = null,
    val cause: Throwable? = null
) {
    AuthServiceInvalidCredentialsException("Invalid Credentials"),
    AuthServiceUserCollisionException("A user with the same credentials already exists"),
    AuthServiceAccountCreationException(),
    AuthServiceInvalidUserException("This user doesn't exist"),
    AuthServiceWeakPasswordException("Weak Password")
}


