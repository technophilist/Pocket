package com.example.pocket.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UrlEntity::class], version = 1, exportSchema = false)
abstract class UrlDatabase : RoomDatabase() {
    companion object {
        private var mInstance: UrlDatabase? = null
        fun getInstance(context: Context) = mInstance ?: synchronized(this) {
            mInstance = Room.databaseBuilder(
                context.applicationContext,
                UrlDatabase::class.java,
                "Pocket_Database"
            ).build()
            mInstance!!
        }

    }

    abstract fun getDao(): Dao
}