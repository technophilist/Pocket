package com.example.pocket.utils

import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * This method is used for getting the resource that was downloaded by glide
 * @param TranscodeType the type of the result(file) that is to be downloaded
 */
suspend fun <TranscodeType> RequestBuilder<TranscodeType>.getDownloadedResource(): TranscodeType {
    return suspendCancellableCoroutine { cancellableContinuation ->
        this.into(object : CustomTarget<TranscodeType>() {
            override fun onResourceReady(
                resource: TranscodeType,
                transition: Transition<in TranscodeType>?
            ) {
                //resuming the coroutine when this method is invoked
                cancellableContinuation.resume(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}

            override fun onLoadFailed(errorDrawable: Drawable?) {
                 cancellableContinuation.resumeWithException(GlideException("Error fetching the image"))
            }
        })
    }
}