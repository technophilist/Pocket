package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.pocket.data.Repository
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
    fun deleteAllUrlItems()
    suspend fun getBitmap(imageAbsolutePathString: String): Bitmap
}

class HomeScreenViewModelImpl(
    private val mRepository: Repository,
    application: Application
) : AndroidViewModel(application), HomeScreenViewModel {
    private var mRecentlyDeletedItem: UrlEntity? = null
    private val _filteredUrlList = MutableLiveData<List<UrlEntity>>(listOf())
    override val filteredList = _filteredUrlList as LiveData<List<UrlEntity>>

    /**
     * Converting the livedata to flow, and, back to a live data.
     * This forces the live data to refresh.
     *
     * The repository instance will remain as long as the app is alive.
     * When the user logs out and [deleteAllUrlItems] is called before
     * navigating to the welcome screen from the home screen, the value
     * of the live data inside the repository will not update to contain
     * an empty list since the observer that used to observe the live data
     * via this viewmodel no longer exists as this viewmodel will be
     * destroyed on navigation. With no observers observing, the live data
     * within the repository contains the previous list which doesn't
     * exist in the database.In order to prevent this, a force refresh is
     * needed.
     */
    override val savedUrls = mRepository.savedUrls.asFlow().asLiveData()

    override fun deleteUrlItem(urlItem: UrlEntity) {
        mRecentlyDeletedItem = savedUrls.value?.let { mRepository.deleteUrl(urlItem) }
    }

    override fun undoDelete() {
        mRecentlyDeletedItem?.let { mRepository.insertUrl(it) }
    }

    override fun onSearchTextValueChange(searchText: String) {
        viewModelScope.launch(Dispatchers.Default) {
            //filtering the list based on the searchText
            val filteredList =
                savedUrls.value?.filter { it.contentTitle.contains(searchText, true) }
            filteredList?.let { _filteredUrlList.postValue(it) }
        }
    }

    override fun deleteAllUrlItems() {
        savedUrls.value?.forEach(::deleteUrlItem)
    }

    override suspend fun getBitmap(imageAbsolutePathString: String): Bitmap =
        withContext(Dispatchers.IO) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
        }
}


