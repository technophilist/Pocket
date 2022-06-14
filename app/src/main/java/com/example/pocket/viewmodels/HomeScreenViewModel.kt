package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pocket.data.Repository
import com.example.pocket.data.domain.SavedUrlItem
import com.example.pocket.di.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val dispatchersProvider: DispatchersProvider,
    application: Application
) : AndroidViewModel(application), HomeScreenViewModel {
    private var recentlyDeletedUrlItem: SavedUrlItem? = null
    private val _filteredUrlItems = MutableLiveData<List<SavedUrlItem>>(listOf())
    override val filteredUrlItems = _filteredUrlItems as LiveData<List<SavedUrlItem>>
    override val savedUrlItems: LiveData<List<SavedUrlItem>> = repository.savedUrlItems

    override fun undoDelete() {
        recentlyDeletedUrlItem?.let { viewModelScope.launch { repository.undoDelete(it) } }
    }

    override fun onSearchTextValueChange(searchText: String) {
        viewModelScope.launch(dispatchersProvider.default) {
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
        withContext(dispatchersProvider.io) {
            File(imageAbsolutePathString)
                .inputStream()
                .use { BitmapFactory.decodeStream(it) }
                .asImageBitmap()
        }
}


