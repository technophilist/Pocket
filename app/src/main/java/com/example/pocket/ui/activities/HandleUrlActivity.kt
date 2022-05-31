package com.example.pocket.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.pocket.R
import com.example.pocket.auth.AuthenticationService
import com.example.pocket.di.PocketApplication
import com.example.pocket.workers.SaveUrlWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HandleUrlActivity : AppCompatActivity() {

    @Inject
    lateinit var authenticationService: AuthenticationService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)

        // if the user is not logged in, don't save the url
        if (!authenticationService.isLoggedIn) {
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
            val dataBuilder = Data.Builder()
            dataBuilder.putString(EXTRA_URL, intent.getStringExtra(Intent.EXTRA_TEXT))
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<SaveUrlWorker>()
                .setInputData(dataBuilder.build())
                .setConstraints(constraints)
                .addTag(SAVE_URL_WORKERS_TAG)
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
            Toast.makeText(
                this,
                resources.getString(R.string.label_added_to_pocket),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

    }

    companion object {
        const val EXTRA_URL = "Url of the html resource"
        const val SAVE_URL_WORKERS_TAG =
            "com.example.pocket.ui.activities.HandleUrlActivity.SAVE_URL_WORKERS_TAG¬"
    }
}