package com.example.pocket.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UrlEntity::class], version = 1, exportSchema = false)
abstract class UrlDatabase : RoomDatabase() {
    companion object {
        private var mInstance: UrlDatabase? = null
        @Synchronized
        fun getInstance(context: Context): UrlDatabase {
            if (mInstance == null)
                mInstance = Room.databaseBuilder(
                    context,
                    UrlDatabase::class.java,
                    "Pocket_Database"
                ).build()
            return mInstance!!
        }
    }

    abstract fun getDao(): Dao
}