package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pocket.data.database.Repository
import com.example.pocket.data.database.UrlEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = Repository.getInstance(application)
    private var mRecentlyDeletedItem: UrlEntity? = null
    private val _filteredUrlList = MutableLiveData<List<UrlEntity>>(listOf())
    val filteredList = _filteredUrlList as LiveData<List<UrlEntity>>
    val savedUrls = mRepository.getUrls

    fun deleteUrlItem(pos: Int) {
        savedUrls.value?.let {
            mRecentlyDeletedItem = it[pos]
            mRepository.deleteUrl(it[pos].id)
        }
    }

    fun undoDelete() {
        mRecentlyDeletedItem?.let {
            mRepository.insertUrl(it)
        }
    }

    fun onSearchTextValueChange(searchText: String) {
        viewModelScope.launch(Dispatchers.Default) {
            filter(searchText)
        }
    }

    private suspend fun filter(searchString: String) {
        val filteredList = withContext(Dispatchers.Default) {
            savedUrls.value?.filter { it.contentTitle.contains(searchString, true) }
        }
        filteredList?.let { _filteredUrlList.postValue(it) }
    }

    suspend fun getBitmap(imageAbsolutePathString: String):Bitmap =
        withContext(Dispatchers.IO) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
        }

}



