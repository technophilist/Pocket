package com.example.pocket.di


import com.example.pocket.auth.AuthenticationService
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.Repository
import com.example.pocket.data.network.Network
import com.example.pocket.data.network.PocketNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetwork(pocketNetwork: PocketNetwork): Network

    @Binds
    @Singleton
    abstract fun bindRepository(
        pocketRepository: PocketRepository
    ): Repository
}