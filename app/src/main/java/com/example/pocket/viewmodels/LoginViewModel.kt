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
     */
    override val authenticationResult = _authenticationResult

    /**
     * This method is used to authenticate a user with the provided [emailAddress] and [password].
     */
    override fun authenticate(emailAddress: String, password: String) {
      CoroutineScope(mDefaultDispatcher).launch {
            //if the email and password are valid, then try signing in the user with the authentication service.
            _authenticationResult.postValue(mAuthenticationService.signIn(emailAddress.trim(), password))
        }
    }
}