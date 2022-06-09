package com.example.pocket.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface Dao {
    @Query("Select * from SavedUrlItems")
    fun getAllUrls(): LiveData<List<UrlEntity>>

    @Query("Delete from SavedUrlItems where id =:id")
    suspend fun deleteUrl(id: Int)

    @Insert
    suspend fun insertUrl(item: UrlEntity)

    @Query("select exists(select url from SavedUrlItems where url =:urlString )")
    suspend fun checkIfUrlExists(urlString: String): Int

    @Query("update SavedUrlItems set isDeleted = 1 where id =:id")
    suspend fun markUrlAsDeleted(id: Int)

    @Query("update SavedUrlItems set isDeleted = 0 where id =:id")
    suspend fun markUrlAsNotDeleted(id: Int)
}