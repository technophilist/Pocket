package com.example.pocket.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UrlEntity::class], version = 1, exportSchema = false)
abstract class UrlDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}