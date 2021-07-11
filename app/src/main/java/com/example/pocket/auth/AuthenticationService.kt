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

sealed class AuthenticationServiceException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    class AuthServiceInvalidCredentialsException(
        message: String? = "Invalid Credentials",
        cause: Throwable? = null
    ) : AuthenticationServiceException(message, cause)

    class AuthServiceUserCollisionException(
        message: String? = "A user with the same credentials already exists",
        cause: Throwable? = null
    ): AuthenticationServiceException(message, cause)


    class AuthServiceAccountCreationException(
        message: String? = null,
        cause: Throwable? = null
    ): AuthenticationServiceException(message, cause)


    class AuthServiceInvalidUserException(
        message: String? = "This user doesn't exist",
        cause: Throwable? = null
    ): AuthenticationServiceException(message, cause)


    class AuthServiceWeakPasswordException(
        message: String? = "Weak Password",
        cause: Throwable? = null
    ): AuthenticationServiceException(message, cause)

}

