package com.example.pocket.viewmodels

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.auth.AuthenticationService
import com.example.pocket.utils.containsDigit
import com.example.pocket.utils.containsLowercase
import com.example.pocket.utils.containsUppercase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InvalidEmailException(message: String? = null) : Exception(message)
class InvalidPasswordException(message: String? = null) : Exception(message)

interface SignUpViewModel {
    val accountCreationResult: LiveData<AuthenticationResult>
    fun createNewAccount(
        name: String,
        email: String,
        password: String,
        profilePhotoUri: Uri? = null
    )
}

class SignUpViewModelImpl(
    private val authenticationService: AuthenticationService,
    private val mDefaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SignUpViewModel {

    private val _accountCreationResult = MutableLiveData<AuthenticationResult>()
    override val accountCreationResult = _accountCreationResult as LiveData<AuthenticationResult>

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
    ) = password.length == 8 && password.containsUppercase() && password.containsLowercase() && password.containsDigit()

    override fun createNewAccount(
        name: String,
        email: String,
        password: String,
        profilePhotoUri: Uri?
    ) {
        if (!isValidEmail(email)) {
            _accountCreationResult.postValue(AuthenticationResult.Failure(InvalidEmailException("Invalid email")))
        }

        if (!isValidPassword(password)) {
            val exceptionMessage = "The password must be of length 8, and must contain atleast one uppercase and lowercase letter and atleast one digit."
            _accountCreationResult.postValue(AuthenticationResult.Failure(InvalidPasswordException(exceptionMessage)))
        }

        CoroutineScope(mDefaultDispatcher).launch {
            val authenticationResult = authenticationService.createAccount(name, email, password, profilePhotoUri)
            _accountCreationResult.postValue(authenticationResult)
        }
    }

}