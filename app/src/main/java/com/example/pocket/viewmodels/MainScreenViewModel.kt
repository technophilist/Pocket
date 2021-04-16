package com.example.pocket.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.pocket.data.database.Repository
import com.example.pocket.data.database.UrlEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = Repository.getInstance(application)
    private var mRecentlyDeletedItem: UrlEntity? = null
    val savedUrls = mRepository.getUrls

    fun getUrlAtPos(pos: Int) = savedUrls.value!![pos].host

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

    suspend fun filter(searchString: String) = withContext(Dispatchers.Default) {
        savedUrls.value?.filter {
            it.contentTitle.contains(searchString, true)
        } ?: listOf()
    }

}
