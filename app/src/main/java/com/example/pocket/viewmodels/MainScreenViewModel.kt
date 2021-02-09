package com.example.pocket.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.example.pocket.data.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = Repository.getInstance(application)
    val savedUrls = Transformations.map(mRepository.getUrls) { it }

    fun deleteUrl(id: Int) = mRepository.deleteUrl(id)
    fun getUrlAtPos(pos: Int) = savedUrls.value!![pos].host
    suspend fun filter(searchString: String) = withContext(Dispatchers.Default) {
        savedUrls.value?.filter { it.contentTitle.contains(searchString, true) }
    }
}
