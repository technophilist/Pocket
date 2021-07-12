package com.example.pocket.auth

sealed class AuthenticationServiceException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

class AuthServiceInvalidEmailException(
    message: String? = "Invalid Email",
    cause: Throwable? = null
):AuthenticationServiceException(message, cause)

class AuthServiceInvalidPasswordException(
    message: String? = "Invalid password",
    cause: Throwable? = null
):AuthenticationServiceException(message, cause)

class AuthServiceInvalidCredentialsException(
    message: String? = "Invalid Credentials",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)

class AuthServiceUserCollisionException(
    message: String? = "A user with the same credentials already exists",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)

class AuthServiceAccountCreationException(
    message: String? = null,
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)

class AuthServiceInvalidUserException(
    message: String? = "This user doesn't exist",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)

class AuthServiceWeakPasswordException(
    message: String? = "Weak Password",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)

class AuthServiceSignInException(
    message: String? = "Unable to sign in",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)
