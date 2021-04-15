package com.example.pocket.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface Dao {

    @Query("Select * from SavedUrlItems")
    fun getAllUrls(): LiveData<List<UrlEntity>>

    @Query("Delete from SavedUrlItems where id =:id")
    suspend fun deleteUrl(id: Int)

    @Insert
    suspend fun insertUrl(item: UrlEntity)



}