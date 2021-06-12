package com.example.pocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pocket.data.database.Dao
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.network.Network
import com.example.pocket.data.preferences.PocketPreferences
import com.example.pocket.data.preferences.PreferencesManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.File
import java.net.URL
import java.util.*

interface Repository {
    val savedUrls: LiveData<List<UrlEntity>>
    val appTheme: LiveData<PocketPreferences.AppTheme>
    suspend fun saveUrl(url: URL)
    fun updateThemePreference(appTheme: PocketPreferences.AppTheme)
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
    private val mUserPreferencesFlow = mPocketPreferencesManger.userPreferences
    private val _appTheme = MutableLiveData(PocketPreferences.AppTheme.SYSTEM)
    private var mRecentThumbnailDeleteJob: Job? = null

    override val savedUrls = mDao.getAllUrls()
    override val appTheme = _appTheme as LiveData<PocketPreferences.AppTheme>

    init {
        mCoroutineScope.launch {
            mUserPreferencesFlow.collect { preferences ->
                _appTheme.postValue(preferences.appTheme)
            }
        }
    }

    /**
     * Used for saving the url,absolute path of the thumbnail and the
     * thumbnail itself to the internal storage.It will save the url
     * only if the url doesn't already exist in the database.Url,absolute
     * path will be stored in the database.Whereas,the thumbnail image will be
     * stored to the internal storage of the device.
     * @param url the complete url of the website
     */
    override suspend fun saveUrl(url: URL) {
        if (!urlExists(url)) {
            val urlContentTitle = mNetwork.fetchWebsiteContentTitle(url)
            val imageAbsolutePath = runCatching {
                saveImageToInternalStorage(
                    mNetwork.downloadImage(url),
                    url.host + urlContentTitle,
                    directoryName = "thumbnails"
                )
            }.getOrNull()

            val faviconPath = runCatching {
                //save the image in png format to preserve the transparent background
                saveImageToInternalStorage(
                    mNetwork.downloadFavicon(url),
                    url.host + urlContentTitle + "favicon",
                    Bitmap.CompressFormat.PNG,
                    "favicons"
                )
            }.getOrNull()

            mDao.insertUrl(
                UrlEntity(
                    url.toString(),
                    urlContentTitle,
                    imageAbsolutePath,
                    faviconPath
                )
            )
        }
    }

    /**
     * Check whether the url already exists in the database or not.
     * @param url the complete url of the website
     * @return true if exists else false
     */
    private suspend fun urlExists(url: URL) =
        when (withContext(mDefaultDispatcher) { mDao.checkIfUrlExists(url.toString()) }) {
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
         means that it is safe to delete the thumbnail and favicon
         images associated with that url from the device storage.
         */
        mRecentThumbnailDeleteJob = mCoroutineScope.launch {
            delay(mLongSnackbarDuration)
            urlItem.imageAbsolutePath?.let { File(it).delete() }
            urlItem.faviconAbsolutePath?.let {
                File(it).delete()
            }
        }
        return urlItem
    }

    override fun updateThemePreference(appTheme: PocketPreferences.AppTheme) {
        mCoroutineScope.launch { mPocketPreferencesManger.updateThemePreference(appTheme) }

    }

    /**
     * Saves the [resource] to the specified directory in the internal storage,
     * in the specified [filetype] using the [fileName] provided.
     *
     * @param resource The resource that is to be stored.
     * @param fileName The name that will be used to save the file.
     * @param filetype Used to specify the type that the file should be saved as.
     * @return If saved successfully,the absolute path of the saved image.Else,null.
     */
    private suspend fun <T> saveImageToInternalStorage(
        resource: T,
        fileName: String,
        filetype: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        directoryName:String
    ): String? {
        val thumbnailsDirectory = File("$mFilesDirectory/$directoryName")
        if (!thumbnailsDirectory.exists()) thumbnailsDirectory.mkdir()
        val bitmapImage = (resource as BitmapDrawable).bitmap

        /*
        * toLowerCase() without any args uses Locale.getDefault() implicitly,which means that it is not locale agnostic
        * this may cause un-expected lowercase conversions
        */
        val imageFile = File(
            "${thumbnailsDirectory.absolutePath}/" + fileName + ".${
                filetype.name.toLowerCase(Locale.ROOT)
            }"
        )

        var savedImagePath: String? = null

        return withContext(mDefaultDispatcher) {
            try {
                imageFile.createNewFile()

                //writing the image to the file using FileOutputStream
                imageFile.outputStream().use {
                    bitmapImage.compress(filetype, 100, it)
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

