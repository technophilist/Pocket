package com.example.pocket.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pocket.data.Repository
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.preferences.PocketPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


interface HomeScreenViewModel {
    val filteredList: LiveData<List<UrlEntity>>
    val savedUrls: LiveData<List<UrlEntity>>
    val currentAppTheme: LiveData<PocketPreferences.AppTheme>

    fun deleteUrlItem(urlItem: UrlEntity)
    fun undoDelete()
    fun onSearchTextValueChange(searchText: String)
    fun changeAppTheme(theme: PocketPreferences.AppTheme)
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
    override val savedUrls = mRepository.savedUrls
    override val currentAppTheme = mRepository.appTheme
    init {
        Timber.d("OInit")
    }

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

    override fun changeAppTheme(theme: PocketPreferences.AppTheme) {
        mRepository.updateThemePreference(theme)
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


