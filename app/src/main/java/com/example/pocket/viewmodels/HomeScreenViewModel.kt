package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.pocket.data.Repository
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.domain.SavedUrlItem
import com.example.pocket.di.DefaultCoroutineDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


interface HomeScreenViewModel {
    @Deprecated(
        message = "Use filteredUrlItemsproperty.",
        replaceWith = ReplaceWith("filteredUrlItems")
    )
    val filteredList: LiveData<List<UrlEntity>>
    val filteredUrlItems: LiveData<List<SavedUrlItem>>

    @Deprecated(
        message = "Used savedUrlItems property instead.",
        replaceWith = ReplaceWith("savedUrlItems")
    )
    val savedUrls: LiveData<List<UrlEntity>>
    val savedUrlItems: LiveData<List<SavedUrlItem>>

    @Deprecated(message = "Use other overload of this method.")
    fun deleteUrlItem(urlItem: UrlEntity)
    fun deleteUrlItem(urlItem: SavedUrlItem)
    fun undoDelete()
    fun onSearchTextValueChange(searchText: String)
    fun deleteAllUrlItems()
    suspend fun getBitmap(imageAbsolutePathString: String): Bitmap
}

@HiltViewModel
class HomeScreenViewModelImpl @Inject constructor(
    private val repository: Repository,
    @DefaultCoroutineDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application
) : AndroidViewModel(application), HomeScreenViewModel {
    private var recentlyDeletedItem: UrlEntity? = null
    private var recentlyDeletedUrlItem: SavedUrlItem? = null
    private val _filteredUrlItems = MutableLiveData<List<SavedUrlItem>>(listOf())
    private val _filteredUrlList = MutableLiveData<List<UrlEntity>>(listOf())

    @Deprecated(
        "Use filteredUrlItemsList property.",
        replaceWith = ReplaceWith("filteredUrlItemsList")
    )
    override val filteredList = _filteredUrlList as LiveData<List<UrlEntity>>
    override val filteredUrlItems = _filteredUrlItems as LiveData<List<SavedUrlItem>>
    //TODO Check this
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
    @Deprecated("Used savedUrlItems property instead.", replaceWith = ReplaceWith("savedUrlItems"))
    override val savedUrls = repository.savedUrls.asFlow().asLiveData()
    override val savedUrlItems: LiveData<List<SavedUrlItem>> = repository.savedUrlItems

    @Deprecated("Use other overload of this method.")
    override fun deleteUrlItem(urlItem: UrlEntity) {
        if (savedUrls.value != null) {
            viewModelScope.launch { recentlyDeletedItem = repository.deleteUrl(urlItem) }
        }
    }

    override fun undoDelete() {
        recentlyDeletedItem?.let { viewModelScope.launch { repository.insertUrl(it) } }
    }

    override fun onSearchTextValueChange(searchText: String) {
        // TODO remove
        viewModelScope.launch(defaultDispatcher) {
            val filteredList = savedUrls.value
                ?.filter { it.contentTitle.contains(searchText, true) }
            filteredList?.let { _filteredUrlList.postValue(it) }
        }
        viewModelScope.launch {
            savedUrlItems.value
                ?.filter { it.title.contains(searchText, true) }
                ?.let { filteredList -> _filteredUrlItems.value = filteredList }
        }
    }

    override fun deleteAllUrlItems() {
        savedUrls.value?.forEach(::deleteUrlItem)
    }

    override fun deleteUrlItem(urlItem: SavedUrlItem) {
        if (savedUrlItems.value != null) {
            viewModelScope.launch {
                recentlyDeletedUrlItem = repository.deleteSavedUrlItem(urlItem)
            }
        }
    }

    override suspend fun getBitmap(imageAbsolutePathString: String): Bitmap =
        withContext(Dispatchers.IO) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
        }
}


