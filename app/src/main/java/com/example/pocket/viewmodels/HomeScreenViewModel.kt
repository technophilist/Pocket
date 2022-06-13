package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pocket.data.Repository
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
    val filteredUrlItems: LiveData<List<SavedUrlItem>>
    val savedUrlItems: LiveData<List<SavedUrlItem>>
    fun deleteUrlItem(urlItem: SavedUrlItem)
    fun undoDelete()
    fun onSearchTextValueChange(searchText: String)
    fun deleteAllUrlItems()
    suspend fun getImageBitmap(imageAbsolutePathString: String): ImageBitmap
}

@HiltViewModel
class HomeScreenViewModelImpl @Inject constructor(
    private val repository: Repository,
    @DefaultCoroutineDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application
) : AndroidViewModel(application), HomeScreenViewModel {
    private var recentlyDeletedUrlItem: SavedUrlItem? = null
    private val _filteredUrlItems = MutableLiveData<List<SavedUrlItem>>(listOf())
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
    override val savedUrlItems: LiveData<List<SavedUrlItem>> = repository.savedUrlItems

    override fun undoDelete() {
        recentlyDeletedUrlItem?.let { viewModelScope.launch { repository.undoDelete(it) } }
    }

    override fun onSearchTextValueChange(searchText: String) {
        viewModelScope.launch(defaultDispatcher) {
            savedUrlItems.value
                ?.filter { it.title.contains(searchText, true) }
                ?.let { filteredList -> _filteredUrlItems.value = filteredList }
        }
    }

    override fun deleteAllUrlItems() {
        savedUrlItems.value?.forEach(::deleteUrlItem)
    }

    override fun deleteUrlItem(urlItem: SavedUrlItem) {
        if (savedUrlItems.value != null) {
            viewModelScope.launch {
                recentlyDeletedUrlItem = repository.deleteSavedUrlItem(urlItem)
            }
        }
    }

    override suspend fun getImageBitmap(imageAbsolutePathString: String): ImageBitmap =
        // TODO hardcoded dispatcher
        withContext(Dispatchers.IO) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
                .asImageBitmap()
        }
}


