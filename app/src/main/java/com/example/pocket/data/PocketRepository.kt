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
import com.example.pocket.data.database.toSavedUrlItem
import com.example.pocket.data.domain.SavedUrlItem
import com.example.pocket.data.domain.toUrlEntity
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
    val savedUrlItems: LiveData<List<SavedUrlItem>>
    val appTheme: LiveData<PocketPreferences.AppTheme>
    suspend fun saveUrlForUser(url: URL)
    suspend fun updateThemePreference(appTheme: PocketPreferences.AppTheme)
    suspend fun deleteSavedUrlItem(savedUrlItem: SavedUrlItem): SavedUrlItem
    suspend fun permanentlyDeleteSavedUrlItem(savedUrlItem: SavedUrlItem)
    suspend fun undoDelete(savedUrlItem: SavedUrlItem)
    suspend fun getUrlItemsMarkedAsDeleted(): List<SavedUrlItem>
}

class PocketRepository @Inject constructor(
    private val network: Network,
    private val dao: Dao,
    private val preferencesManager: PreferencesManager,
    @IoCoroutineDispatcher private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @ApplicationContext context: Context
) : Repository {
    private val filesDirectory = context.filesDir
    private val userPreferencesFlow = preferencesManager.userPreferences
    override val savedUrlItems = dao.getAllUrls().map { urlEntityList ->
        urlEntityList.map { it.toSavedUrlItem() }
    }
    override val appTheme = userPreferencesFlow.asLiveData().map { it.appTheme }

    /**
     * Used for saving the [url],and the associated favicon and thumbnail.
     * It will save the url only if it doesn't already exist in the
     * database.The URL and the absolute paths of the favicon and the thumbnail
     * will be stored in the database.Whereas,the thumbnail image and the favicon
     * image will be stored in the internal storage of the device.
     */
    override suspend fun saveUrlForUser(url: URL) {
        if (urlExists(url)) return
        // if there is not content title, use the url as the content title.
        val urlContentTitle = network.fetchWebsiteContentTitle(url) ?: url.toString()
        /* Download the image that will be used as the thumbnail,save to the internal storage and get the path
         * to the location where the image was downloaded
         */
        val imageAbsolutePath: String? =
            network.fetchThumbnail(url)?.let { thumbnailDrawable ->
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
        val urlEntity = UrlEntity(
            url = url.toString(),
            contentTitle = urlContentTitle,
            imageAbsolutePath = imageAbsolutePath,
            faviconAbsolutePath = faviconPath
        )
        dao.insertUrl(urlEntity)
    }

    /**
     * Check whether the url already exists in the database or not.
     * @param url the complete url of the website
     * @return true if exists else false
     */
    private suspend fun urlExists(url: URL) =
        when (dao.checkIfUrlExists(url.toString())) {
            0 -> false
            else -> true
        }

    /**
     * Used for deleting the url from the database.
     * Note: This method removes only the [savedUrlItem] from the
     * database. It **does not**  remove the associated favicon
     * and thumbnails stored in the device's internal storage.
     * @param savedUrlItem the url item to be deleted
     * @return the deleted url item
     */
    override suspend fun deleteSavedUrlItem(savedUrlItem: SavedUrlItem): SavedUrlItem {
        val urlItem = savedUrlItem.toUrlEntity()
        dao.markUrlAsDeleted(urlItem.id)
        return savedUrlItem
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
            val directory = File("$filesDirectory/$directoryName")
            if (!directory.exists()) directory.mkdir()
            val fileExtension = filetype.name.toLowerCase(Locale.ROOT)
            val imageFile = File("${directory.absolutePath}/" + fileName + ".$fileExtension")
            imageFile.createNewFile()
            val imageAbsolutePath = imageFile.outputStream().use {
                bitmapImage.compress(filetype, 100, it)
                imageFile.absolutePath
            }
            imageAbsolutePath
        }.getOrNull()
    }

    /**
     * Used to undo the deletion of the [savedUrlItem].
     */
    override suspend fun undoDelete(savedUrlItem: SavedUrlItem) {
        dao.markUrlAsNotDeleted(savedUrlItem.toUrlEntity().id)
    }

    /**
     * Used to get a list of all [SavedUrlItem]'s marked as 'deleted'
     * in the database.
     */
    override suspend fun getUrlItemsMarkedAsDeleted(): List<SavedUrlItem> =
        dao.getAllUrlsMarkedAsDeleted().map { it.toSavedUrlItem() }

    /**
     * Used to permanently delete the specified [savedUrlItem].
     * It not only deletes the item from the database, but, also
     * deletes the thumbnail and favicon images stored in the device's
     * internal storage.
     */
    override suspend fun permanentlyDeleteSavedUrlItem(savedUrlItem: SavedUrlItem) {
        with(savedUrlItem.toUrlEntity()) {
            imageAbsolutePath?.let { File(it).delete() }
            faviconAbsolutePath?.let { File(it).delete() }
            dao.deleteUrl(this)
        }
    }
}

