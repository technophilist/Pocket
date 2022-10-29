package com.example.pocket.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Query("select * from SavedUrlItems where isDeleted = 1")
    suspend fun getAllUrlsMarkedAsDeleted(): List<UrlEntity>

    @Query("select * from SavedUrlItems where isDeleted = 0 and associatedUserId = :userId")
    fun getAllUrlsForUserId(userId: String): LiveData<List<UrlEntity>>

    @Insert
    suspend fun insertUrl(item: UrlEntity)

    @Delete
    suspend fun deleteUrl(item: UrlEntity)

    @Query("update SavedUrlItems set isDeleted = 1 where id =:id")
    suspend fun markUrlAsDeleted(id: Int)

    @Query("update SavedUrlItems set isDeleted = 0 where id =:id")
    suspend fun markUrlAsNotDeleted(id: Int)

    @Query("select * from SavedUrlItems where url =:urlString")
    suspend fun getUrlEntityWithUrl(urlString: String): UrlEntity?
}