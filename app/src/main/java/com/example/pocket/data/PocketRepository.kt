package com.example.pocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.pocket.data.database.Dao
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.data.network.Network
import com.example.pocket.data.preferences.PocketPreferences
import com.example.pocket.data.preferences.PreferencesManager
import com.example.pocket.di.IoCoroutineDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.util.*
import javax.inject.Inject

interface Repository {
    val savedUrls: LiveData<List<UrlEntity>>
    val appTheme: LiveData<PocketPreferences.AppTheme>
    suspend fun saveUrl(url: URL)
    suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme)
    suspend fun deleteUrl(urlItem: UrlEntity): UrlEntity
    suspend fun insertUrl(urlItem: UrlEntity)
}

class PocketRepository @Inject constructor(
    private val network: Network,
    private val dao: Dao,
    private val preferencesManager: PreferencesManager,
    @IoCoroutineDispatcher private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @ApplicationContext context: Context
) : Repository {
    private val mFilesDirectory = context.filesDir
    private val mLongSnackbarDuration = 10_000L
    private val mUserPreferencesFlow = preferencesManager.userPreferences
    private var mRecentThumbnailDeleteJob: Job? = null
    override val savedUrls = dao.getAllUrls()
    override val appTheme = mUserPreferencesFlow.asLiveData().map { it.appTheme }

    /**
     * Used for saving the [url],and the associated favicon and thumbnail.
     * It will save the url only if it doesn't already exist in the
     * database and the content title is not null.The URL and the absolute
     * paths of the favicon and the thumbnail will be stored in the database.
     * Whereas,the thumbnail image and the favicon image will be stored in
     * the internal storage of the device.
     */
    override suspend fun saveUrl(url: URL) {
        // save the url only if it doesn't exist
        if (!urlExists(url)) {
            // get the content title of the webpage
            val urlContentTitle = network.fetchWebsiteContentTitle(url)

            // save the url only if the urlContentTitle is not null
            if (urlContentTitle != null) {

                /* Download the image that will be used as the thumbnail,save to the internal storage and get the path
                 * to the location where the image was downloaded
                 */
                val imageAbsolutePath: String? =
                    network.fetchImage(url)?.let { thumbnailDrawable ->
                        saveImageToInternalStorage(
                            resource = thumbnailDrawable,
                            fileName = url.host + urlContentTitle,
                            directoryName = "thumbnails"
                        )
                    }

                // download the favicon,save to the internal storage and get the path to the location where the image was downloaded
                val faviconPath: String? = network.fetchFavicon(url)?.let { faviconDrawable ->
                    saveImageToInternalStorage(
                        resource = faviconDrawable,
                        fileName = url.host + urlContentTitle + "favicon",
                        filetype = Bitmap.CompressFormat.PNG,
                        directoryName = "favicons"
                    )
                }

                // save it to the database
                dao.insertUrl(
                    UrlEntity(
                        url.toString(),
                        urlContentTitle,
                        imageAbsolutePath,
                        faviconPath
                    )
                )
            }
        }
    }

    /**
     * Check whether the url already exists in the database or not.
     * @param url the complete url of the website
     * @return true if exists else false
     */
    private suspend fun urlExists(url: URL) =
        when (withContext(defaultDispatcher) { dao.checkIfUrlExists(url.toString()) }) {
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
    override suspend fun deleteUrl(urlItem: UrlEntity): UrlEntity {
        /*
         Jetpack compose doesn't support item delete animations for lazy lists.So it
         becomes necessary to delete the item from the database and re-insert it if the
         user clicks on the undo action of the snackBar.
        * */
        dao.deleteUrl(urlItem.id)

        /*
         If mRecentThumbnailDeleteJob is not null and a new
         Job is assigned to it, it means that the undo delete
         snack bar for that particular url was dismissed.Which
         means that it is safe to delete the thumbnail and favicon
         images associated with that url from the device storage.
         */
        mRecentThumbnailDeleteJob = coroutineScope {
            launch {
                delay(mLongSnackbarDuration)
                urlItem.imageAbsolutePath?.let { File(it).delete() }
                urlItem.faviconAbsolutePath?.let {File(it).delete() }
            }
        }
        return urlItem
    }

    override suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme) {
        preferencesManager.updateThemePreference(appTheme)
    }

    /**
     * Saves the [resource] as a bitmap to the specified directory in the internal
     * storage,in the specified [filetype] using the [fileName] provided.
     *
     * @param resource The resource that is to be stored.
     * @param fileName The name that will be used to save the file.
     * @param filetype Used to specify the type that the file should be saved as.
     * @return If saved successfully,the absolute path of the saved image.Else,null.
     */
    private suspend fun <T : Drawable> saveImageToInternalStorage(
        resource: T,
        fileName: String,
        filetype: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        directoryName: String
    ): String? = withContext(defaultDispatcher) {
        runCatching {

            val bitmapImage = (resource as BitmapDrawable).bitmap

            //creating a file instance with the specified path
            val directory = File("$mFilesDirectory/$directoryName")

            //create the directory only if it doesn't already exist
            if (!directory.exists()) directory.mkdir()

            //string representing the path of the saved resource
            var savedPath: String?

            /*
             * toLowerCase() without any args uses Locale.getDefault() implicitly,which means that it is not locale agnostic
             * this may cause un-expected lowercase conversions
             */
            val fileExtension = filetype.name.toLowerCase(Locale.ROOT)
            val imageFile = File("${directory.absolutePath}/" + fileName + ".$fileExtension")

            //create a new image file
            imageFile.createNewFile()

            //writing the image to the file using FileOutputStream
            imageFile.outputStream().use {
                bitmapImage.compress(filetype, 100, it)
                savedPath = imageFile.absolutePath
            }

            //return the path of the created file
            savedPath

        }.getOrElse {

            //if any exception occurs,return null
            null
        }
    }


    override suspend fun insertUrl(urlItem: UrlEntity) {
        dao.insertUrl(urlItem)
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

