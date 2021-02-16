package com.example.pocket.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pocket.R
import com.example.pocket.workers.SaveUrlWorker


class HandleUrlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
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
                "Added to Pocket",
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