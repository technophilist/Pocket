package com.example.pocket.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @param context the context
 * @param imageUrl the string representation of the url of the image
 * @return null if some error occurred while downloading
 */
suspend fun downloadImage(context: Context, imageUrl: String):Drawable? {
    return withContext(Dispatchers.IO) {
        try{
        Glide.with(context)
            .load(imageUrl)
            .submit()
            .get()
        }catch (exception:Exception){
            null
        }
    }
}


