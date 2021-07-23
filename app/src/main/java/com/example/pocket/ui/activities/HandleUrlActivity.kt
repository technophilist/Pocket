package com.example.pocket.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pocket.R
import com.example.pocket.di.PocketApplication
import com.example.pocket.workers.SaveUrlWorker


class HandleUrlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        val appContainer = (applicationContext as PocketApplication).appContainer

        // if the user is not logged in, don't save the url
        if (!appContainer.authenticationService.isLoggedIn) {
            Toast.makeText(
                this,
                resources.getString(R.string.label_login_to_save_files),
                Toast.LENGTH_LONG
            ).show()
            finish()
            /*
             * An explicit return statement should be used because
             * activity will not be immediately finished but only be
             * planned to be 'finished'. Once the looper reaches the statement,
             * it will begin the de-initialization sequence.
             */
            return
        }
        if (intent.type == "text/plain") {

            //Building the data using data builder
            val dataBuilder = Data.Builder()
            dataBuilder.putString(EXTRA_URL, intent.getStringExtra(Intent.EXTRA_TEXT))

            //Scheduling the work manager to save the url
            val workRequest = OneTimeWorkRequestBuilder<SaveUrlWorker>()
                .setInputData(dataBuilder.build())
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)

            //displaying toast
            Toast.makeText(
                this,
                resources.getString(R.string.label_added_to_pocket),
                Toast.LENGTH_SHORT
            ).show()

            //Closing the activity
            finish()
        }

    }

    companion object {
        const val EXTRA_URL = "Url of the html resource"
    }
}