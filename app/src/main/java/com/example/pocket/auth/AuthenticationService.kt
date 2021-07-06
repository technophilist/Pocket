package com.example.pocket.auth

interface AuthenticationService {
    suspend fun signIn(email: String, password: String):AuthenticationResult
    suspend fun createAccount(email: String, password: String):AuthenticationResult
}

sealed class AuthenticationResult {
    data class Success(val user: User) : AuthenticationResult()
    data class Failure(val exception: Exception) : AuthenticationResult()
}


