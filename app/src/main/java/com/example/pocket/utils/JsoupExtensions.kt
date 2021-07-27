package com.example.pocket.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Connection
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Interface containing callback functions that will be used
 * when fetching the document in the background.
 */
private interface OnGetDocument {
    fun onSuccess(document: Document)
    fun onFailure(exception: Exception)
}

/**
 * Fetch the document in a background thread.
 *
 * This method is used for getting the document object on
 * a background thread.It uses an [executor] to move the
 * work to a background thread and a [resultHandler] that
 * executes the [callback] on the main thread.
 */
private fun Connection.fetchDocument(
    callback: OnGetDocument,
    executor: Executor = Executors.newSingleThreadExecutor(),
    resultHandler: Handler = Handler(Looper.getMainLooper()),
) {
    executor.execute {
        try {

            //fetch the document
            val document = this.get()

            // run the callback on the main thread
            resultHandler.post { callback.onSuccess(document) }

        } catch (exception: Exception) {

            // run callback on the main thread
            resultHandler.post { callback.onFailure(exception) }

        }
    }
}

/**
 * Suspend function for getting the document.
 *
 * Jsoup library doesn't provide a suspend implementation for
 * getting the document object.The default get() method blocks
 * the thread.This method provides a suspend implementation for
 * getting the document object from the connection.It will return
 * the document object fetched from the connection.If an [Exception]
 * occurs,it will throw the exception.
 * <p>
 * Usage :
 * val document = Jsoup
 *       .connect(url.toString())
 *       .getDocument()
 *</p>
 */
suspend fun Connection.getDocument(): Document =
    suspendCancellableCoroutine { cancellableContinuation ->
        fetchDocument(object : OnGetDocument {
            override fun onSuccess(document: Document) {
                //if success,resume with the document if success
                cancellableContinuation.resume(document)
            }

            override fun onFailure(exception: Exception) {
                //resume with an exception when an exception occurs
                cancellableContinuation.resumeWithException(exception)
            }
        })
    }

