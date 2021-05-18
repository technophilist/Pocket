package com.example.pocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import com.example.pocket.data.database.Dao
import com.example.pocket.data.database.UrlDatabase
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.network.Network
import com.example.pocket.data.network.PocketNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

interface Repository{
    val getUrls:LiveData<List<UrlEntity>>
    suspend fun saveUrl(urlString: String, thumbnail: Drawable?)
    fun deleteUrl(urlItem: UrlEntity): UrlEntity
    fun insertUrl(urlItem: UrlEntity)
}

class PocketRepository(
    private val mNetwork: Network,
    private val mDao:Dao,
    context: Context
):Repository {
    private val mFilesDirectory = context.filesDir
    private val mCoroutineScope = CoroutineScope(Dispatchers.IO)
    override val getUrls = mDao.getAllUrls()

    /**
     * Used for saving the url,absolute path of the thumbnail and the
     * thumbnail itself to the internal storage.It will save the url
     * only if the url doesn't already exist in the database.Url,absolute
     * path will be stored in the database.Whereas,the thumbnail image will be
     * stored to the internal storage of the device.
     * @param urlString string representing the complete url
     * @param thumbnail the drawable image that will be used for the thumbnail
     */
    override suspend fun saveUrl(urlString: String, thumbnail: Drawable?) {
        if (!urlExists(urlString)) {
            val url = URL(urlString)
            val urlContentTitle = mNetwork.fetchWebsiteContentTitle(urlString)
            val imageAbsolutePath = runCatching { saveImageToInternalStorage(thumbnail, url.host + urlContentTitle) }.getOrNull()
            mDao.insertUrl(UrlEntity(urlString, urlContentTitle, imageAbsolutePath))
        }
    }

    /**
     * Check whether the url already exists in the database or not.
     * @param urlString string representing the complete url
     * @return true if exists else false
     */
    private suspend fun urlExists(urlString: String) =
        when (withContext(Dispatchers.IO) { mDao.checkIfUrlExists(urlString) }) {
            0 -> false
            else -> true
        }

    override fun deleteUrl(urlItem: UrlEntity): UrlEntity {
        mCoroutineScope.launch {
            urlItem.imageAbsolutePath?.let { File(it).delete() }
            mDao.deleteUrl(urlItem.id)
        }
        return urlItem
    }

    /**
     * Saves the [resource] as a jpg file to internal storage
     * @param resource the type of image resource that is to be stored
     * @param fileName the name that will be used to save the file
     * @return If saved successfully,the absolute path of the saved image.Else,
     *         null.
     */
    private suspend fun <T> saveImageToInternalStorage(
        resource: T,
        fileName: String
    ): String? {
        val thumbnailsDirectory = File("$mFilesDirectory/thumbnails")
        if (!thumbnailsDirectory.exists()) thumbnailsDirectory.mkdir()

        val bitmapImage = (resource as BitmapDrawable).bitmap
        val imageFile = File("${thumbnailsDirectory.absolutePath}/" + fileName + ".jpeg")
        var savedImagePath: String? = null

        return withContext(Dispatchers.IO) {
            try {
                imageFile.createNewFile()

                //writing the image to the file using FileOutputStream
                imageFile.outputStream().use {
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    savedImagePath = imageFile.absolutePath
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            savedImagePath
        }
    }

    override fun insertUrl(urlItem: UrlEntity) {
        mCoroutineScope.launch { mDao.insertUrl(urlItem) }
    }

}



