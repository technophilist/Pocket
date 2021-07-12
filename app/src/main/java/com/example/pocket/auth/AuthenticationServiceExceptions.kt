package com.example.pocket.auth

/**
 * The sub classes of this sealed class will be used by [AuthenticationResult.Failure].If a new kind of exception,
 * related to authentication is to be created, then that exception will have to extend this sealed class in order to
 * be used in conjunction with [AuthenticationResult.Failure].
 *
 * The [AuthenticationResult] class is the model class that is used for all authentication purposes in the entire app.
 * This allows to easily switch out the [AuthenticationService] without breaking other code that depends up the exceptions
 * thrown by the different implementations of [AuthenticationService].
 *
 * It must also be ensured that any implementation of [AuthenticationService] that makes use of [AuthenticationResult] must only throw
 * exceptions that are subclasses of this class.
 */
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

class AuthServiceSignInException(
    message: String? = "Unable to sign in",
    cause: Throwable? = null
) : AuthenticationServiceException(message, cause)
