package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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


interface HomeScreenViewModel {
    val filteredList: LiveData<List<UrlEntity>>
    val savedUrls: LiveData<List<UrlEntity>>

    fun deleteUrlItem(urlItem: UrlEntity)
    fun undoDelete()
    fun onSearchTextValueChange(searchText: String)
    suspend fun getBitmap(imageAbsolutePathString: String): Bitmap
}

private const val TAG = "HomeScreenViewModelImpl"
class HomeScreenViewModelImpl(application: Application) : AndroidViewModel(application),HomeScreenViewModel {
    private val mRepository = Repository.getInstance(application)
    private var mRecentlyDeletedItem: UrlEntity? = null
    private val _filteredUrlList = MutableLiveData<List<UrlEntity>>(listOf())
    override val filteredList = _filteredUrlList as LiveData<List<UrlEntity>>
    override val savedUrls = mRepository.getUrls

    override fun deleteUrlItem(urlItem:UrlEntity) {
        savedUrls.value?.let {
            //TODO REMOVE THE THUMBNAIL IMAGE FROM INTERNAL STORAGE WHEN DELETING
            val indexOfDeletedItem = it.indexOf(urlItem)
            mRecentlyDeletedItem = it[indexOfDeletedItem]
            mRepository.deleteUrl(urlItem.id)
        }
    }

    override fun undoDelete() {
        mRecentlyDeletedItem?.let { mRepository.insertUrl(it) }
    }

    override fun onSearchTextValueChange(searchText: String) {
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

    override suspend fun getBitmap(imageAbsolutePathString: String):Bitmap =
        withContext(Dispatchers.IO) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
        }

}



