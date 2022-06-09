package com.example.pocket.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Query("select * from SavedUrlItems where isDeleted = 0")
    fun getAllUrls(): LiveData<List<UrlEntity>>

    @Query("select * from SavedUrlItems where isDeleted = 1")
    suspend fun getAllUrlsMarkedAsDeleted(): List<UrlEntity>

    @Insert
    suspend fun insertUrl(item: UrlEntity)

    @Delete
    suspend fun deleteUrl(item: UrlEntity)

    @Query("select exists(select url from SavedUrlItems where url =:urlString )")
    suspend fun checkIfUrlExists(urlString: String): Int

    @Query("update SavedUrlItems set isDeleted = 1 where id =:id")
    suspend fun markUrlAsDeleted(id: Int)

    @Query("update SavedUrlItems set isDeleted = 0 where id =:id")
    suspend fun markUrlAsNotDeleted(id: Int)
}