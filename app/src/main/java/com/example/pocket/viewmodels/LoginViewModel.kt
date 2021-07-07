package com.example.pocket.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.auth.AuthenticationService
import com.example.pocket.utils.containsDigit
import com.example.pocket.utils.containsLowercase
import com.example.pocket.utils.containsUppercase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * These two classes are custom [Exception] classes that will be used
 * in the [LoginViewModel.authenticationResult].
 */
class InvalidEmailException(message: String? = null) : Exception(message)
class InvalidPasswordException(message: String? = null) : Exception(message)

interface LoginViewModel {
    val authenticationResult: LiveData<AuthenticationResult>
    fun authenticate(emailAddress: String, password: String)
}

class LoginViewModelImpl(
    private val mAuthenticationService: AuthenticationService,
    private val mDefaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), LoginViewModel {

    private val _authenticationResult = MutableLiveData<AuthenticationResult>()

    /**
     * This value is a livedata that contains an instance of [AuthenticationResult].If the
     * authentication is successful, it returns an instance of [AuthenticationResult.Success]
     * with the authenticated user in it. Else, it returns an instance of [AuthenticationResult.Failure]
     * with the exception that caused the failure.
     *
     * One major use-case is to check whether the the exception was cause by an invalid username or password.
     * Example usage to get check whether the exception was cause by an invalid password or username:
     * ```
     * when (authenticationResult.exception) {
     *   is InvalidEmailException -> { //Do something }
     *   is InvalidPasswordException -> { //Do something }
     *  }
     *```
     */
    override val authenticationResult = _authenticationResult

    /**
     * The method is used to check whether the [email] is valid .An email is valid
     * if, and only if, it is not blank(ie. is not empty and doesn't contain whitespace characters)
     * and matches the [Patterns.EMAIL_ADDRESS] regex.
     */
    private fun isValidEmail(email: String) =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * The method is used to check whether the [password] is valid.A password is valid if, and only if,
     * it is of length 8 , contains atleast one uppercase and lowercase letter and contains atleast one digit.
     */
    private fun isValidPassword(
        password: String
    ) =
        password.length == 8 && password.containsUppercase() && password.containsLowercase() && password.containsDigit()

    /**
     * This method is used to authenticate a user with the provided [emailAddress] and [password].
     */
    override fun authenticate(emailAddress: String, password: String) {
        if (!isValidEmail(emailAddress))
            _authenticationResult.value = AuthenticationResult.Failure(InvalidEmailException())
        else if (!isValidPassword(password))
            _authenticationResult.value = AuthenticationResult.Failure(InvalidPasswordException())
        else CoroutineScope(mDefaultDispatcher).launch {
            //if the email and password are valid, then try signing in the user with the authentication service.
            _authenticationResult.postValue(mAuthenticationService.signIn(emailAddress, password))
        }
    }
}