package com.example.pocket.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pocket.auth.AuthenticationService
import com.example.pocket.data.Repository
import com.example.pocket.viewmodels.*
import kotlinx.coroutines.CoroutineDispatcher


class HomeScreenViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.AndroidViewModelFactory(application) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeScreenViewModelImpl(repository, application) as T
}


class LoginViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = LoginViewModelImpl(authenticationService, defaultDispatcher) as T
}

class SignUpViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val defaultDispatcher: CoroutineDispatcher
):ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = SignUpViewModelImpl(authenticationService,defaultDispatcher) as T
}