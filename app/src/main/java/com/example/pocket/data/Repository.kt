package com.example.pocket.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.pocket.utils.downloadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Repository private constructor(private val mContext: Context) {
    private val mDatabase = UrlDatabase.getInstance(mContext)
    private val mDao = mDatabase.getDao()
    val getUrls: LiveData<List<UrlEntity>> = mDao.getAllUrls()

    companion object {
        private const val TAG = "Repository"
        private var mInstance: Repository? = null

        @Synchronized
        fun getInstance(context: Context): Repository {
            if (mInstance == null) mInstance = Repository(context)
            return mInstance!!
        }
    }

    fun saveUrl(url: String, urlContentTitle: String, imageUrlString: String?) {
        CoroutineScope(Dispatchers.IO).launch {

            val imageAbsolutePath = imageUrlString?.let {

                //Downloading the image as a drawable
                val imageDrawable = downloadImage(mContext, imageUrlString)

                //Selecting the last 5 characters as the fileName
                val imageFileName = imageUrlString.substring(imageUrlString.length - 5)

                //Getting the absolute path of the image
                imageDrawable?.let {
                    saveImageToInternalStorage(
                        mContext,
                        imageDrawable,
                        imageFileName
                    )
                }

            }

            //Inserting the url entity into the database
            mDao.insertUrl(UrlEntity(url,urlContentTitle,imageAbsolutePath))
        }
    }

    fun deleteUrl(id: Int) {
        CoroutineScope(Dispatchers.IO).launch { mDao.deleteUrl(id) }
    }


    /**
     * Saves the [resource] as a jpg file to internal storage
     * @param resource the type of image resource that is to be stored
     * @param fileName the name that will be used to save the file
     * @return If saved successfully,the absolute path of the saved image.Else,
     *         null.
     */
    private suspend fun <T> saveImageToInternalStorage(
        context: Context,
        resource: T,
        fileName: String
    ): String? {
        val thumbnailsDirectory = File("${context.filesDir}/thumbnails")
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
}