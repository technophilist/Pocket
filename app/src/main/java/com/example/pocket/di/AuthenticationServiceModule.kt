package com.example.pocket.di

import com.example.pocket.auth.AuthenticationService
import com.example.pocket.auth.FirebaseAuthenticationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthenticationServiceModule {
    @Binds
    abstract fun bindAuthenticationService(
        firebaseAuthenticationService: FirebaseAuthenticationService
    ): AuthenticationService
}