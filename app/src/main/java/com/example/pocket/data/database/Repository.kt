package com.example.pocket.data.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.pocket.data.network.PocketNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class Repository private constructor(context: Context) {
    private val mDatabase = UrlDatabase.getInstance(context)
    private val mDao = mDatabase.getDao()
    private val mNetwork = PocketNetwork.getInstance()
    private val mFilesDirectory = context.filesDir
    private val mCoroutineScope = CoroutineScope(Dispatchers.IO)
    val getUrls = mDao.getAllUrls()

    /**
     * Used for saving the url,absolute path of the thumbnail and the
     * thumbnail itself to the internal storage.It will save the url
     * only if the url doesn't already exist in the database.Url,absolute
     * path will be stored in the database.Whereas,the thumbnail image will be
     * stored to the internal storage of the device.
     * @param urlString string representing the complete url
     * @param thumbnail the drawable image that will be used for the thumbnail
     */
    suspend fun saveUrl(urlString: String, thumbnail: Drawable?) {
        if (!urlExists(urlString)) {
            val url = URL(urlString)
            val urlContentTitle = mNetwork.fetchWebsiteContentTitle(urlString)
            val imageAbsolutePath =
                thumbnail?.let { saveImageToInternalStorage(thumbnail, url.host + urlContentTitle) }
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

    fun deleteUrl(id: Int) {
        mCoroutineScope.launch { mDao.deleteUrl(id) }
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
                FileOutputStream(imageFile).use {
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    savedImagePath = imageFile.absolutePath
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            savedImagePath
        }
    }

    fun insertUrl(urlItem: UrlEntity) {
        mCoroutineScope.launch { mDao.insertUrl(urlItem) }
    }

    companion object {
        private var mInstance: Repository? = null
        fun getInstance(context: Context) = mInstance ?: synchronized(this) {
            mInstance = Repository(context)
            mInstance!!
        }
    }
}



