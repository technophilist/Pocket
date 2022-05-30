package com.example.pocket.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier


@Qualifier
annotation class DefaultCoroutineDispatcher

@Qualifier
annotation class IoCoroutineDispatcher

@Qualifier
annotation class MainCoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @Provides
    @DefaultCoroutineDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @IoCoroutineDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainCoroutineDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}