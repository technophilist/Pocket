package com.example.pocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import com.example.pocket.data.database.Dao
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.network.Network
import com.example.pocket.data.preferences.PocketPreferences
import com.example.pocket.data.preferences.PreferencesManager
import com.example.pocket.data.preferences.UserPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.net.URL

interface Repository {
    val savedUrls: LiveData<List<UrlEntity>>
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun saveUrl(urlString: String)
    suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme)
    fun deleteUrl(urlItem: UrlEntity): UrlEntity
    fun insertUrl(urlItem: UrlEntity)
}

class PocketRepository(
    private val mNetwork: Network,
    private val mDao: Dao,
    private val mPocketPreferencesManger: PreferencesManager,
    private val mDefaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    context: Context
) : Repository {
    private val mFilesDirectory = context.filesDir
    private val mCoroutineScope = CoroutineScope(mDefaultDispatcher)
    private val mLongSnackbarDuration = 10_000L
    private var mRecentThumbnailDeleteJob: Job? = null
    override val savedUrls = mDao.getAllUrls()
    override val userPreferencesFlow = mPocketPreferencesManger.userPreferences

    /**
     * Used for saving the url,absolute path of the thumbnail and the
     * thumbnail itself to the internal storage.It will save the url
     * only if the url doesn't already exist in the database.Url,absolute
     * path will be stored in the database.Whereas,the thumbnail image will be
     * stored to the internal storage of the device.
     * @param urlString string representing the complete url
     */
    override suspend fun saveUrl(urlString: String) {
        if (!urlExists(urlString)) {
            val url = URL(urlString)
            val urlContentTitle = mNetwork.fetchWebsiteContentTitle(urlString)
            val imageAbsolutePath = runCatching {
                saveImageToInternalStorage(
                    mNetwork.downloadImage(urlString),
                    url.host + urlContentTitle
                )
            }.getOrNull()
            mDao.insertUrl(UrlEntity(urlString, urlContentTitle, imageAbsolutePath))
        }
    }

    /**
     * Check whether the url already exists in the database or not.
     * @param urlString string representing the complete url
     * @return true if exists else false
     */
    private suspend fun urlExists(urlString: String) =
        when (withContext(mDefaultDispatcher) { mDao.checkIfUrlExists(urlString) }) {
            0 -> false
            else -> true
        }

    /**
     * Used for deleting the url from the database.
     * Even though the url entity will be deleted immediately, the thumbnail
     * of the url will remain in the devices' internal storage for
     * [mLongSnackbarDuration] seconds before getting deleted.
     * @param urlItem the url item to be deleted
     * @return the deleted url item
     */
    override fun deleteUrl(urlItem: UrlEntity): UrlEntity {
        /*
         Jetpack compose doesn't support item delete animations for lazy lists.So it
         becomes necessary to delete the item from the database and re-insert it if the
         user clicks on the undo action of the snackBar.
        * */
        mCoroutineScope.launch { mDao.deleteUrl(urlItem.id) }

        /*
        If mRecentThumbnailDeleteJob is not null and a new
        Job is assigned to it, it means that the undo delete
        snack bar for that particular url was dismissed.Which
        means that it is safe to delete that thumbnail from the
        device storage.
         */
        mRecentThumbnailDeleteJob = mCoroutineScope.launch {
            delay(mLongSnackbarDuration)
            urlItem.imageAbsolutePath?.let { File(it).delete() }
        }
        return urlItem
    }

    override suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme) {
        mPocketPreferencesManger.updateThemePreference(appTheme)
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

        return withContext(mDefaultDispatcher) {
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
        if (mRecentThumbnailDeleteJob?.isActive == true) {
            /*
            If it is active it means this function was called as
            a result of the user clicking the 'undo' button
            of the snack bar.
             */

            //Cancelling the job , so it doesn't delete the thumbnail
            mRecentThumbnailDeleteJob?.cancel()
        }

    }
}

