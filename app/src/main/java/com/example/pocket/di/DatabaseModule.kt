package com.example.pocket.di

import android.content.Context
import androidx.room.Room
import com.example.pocket.data.database.Dao
import com.example.pocket.data.database.UrlDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "Pocket_Database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext applicationContext: Context): UrlDatabase =
        Room.databaseBuilder(
            applicationContext,
            UrlDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    fun provideDao(urlDatabase: UrlDatabase): Dao = urlDatabase.getDao()
}