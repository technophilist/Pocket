package com.example.pocket.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.pocket.di.PocketApplication
import com.example.pocket.ui.screens.HomeScreen
import com.example.pocket.ui.theme.PocketAppTheme
import com.example.pocket.utils.HomeScreenViewModelFactory
import com.example.pocket.viewmodels.HomeScreenViewModel
import com.example.pocket.viewmodels.HomeScreenViewModelImpl


class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: HomeScreenViewModel

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as PocketApplication).appContainer
        mViewModel = ViewModelProvider(
            this,
            HomeScreenViewModelFactory(application, appContainer.pocketRepository)
        ).get(HomeScreenViewModelImpl::class.java)

        setContent { PocketApp() }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun PocketApp() {
        val isDarkModeSupported = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
        var isDarkModeEnabled by remember { mutableStateOf(false) }
        PocketAppTheme(
            if(isDarkModeSupported) isSystemInDarkTheme() else isDarkModeEnabled
        ) {
            Surface(Modifier.fillMaxSize()) {
                HomeScreen(
                    viewModel = mViewModel,
                    onClickUrlItem = { openUrl(it.url) },
                    isDarkModeSupported = isDarkModeSupported,
                    onDarkModeIconClicked = { isDarkModeEnabled = !isDarkModeEnabled },
                    isDarkModeEnabled = isDarkModeEnabled
                )
            }
        }
    }

    private fun openUrl(urlString: String) {
        val uri = Uri.parse(urlString)
        val openLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(openLinkIntent)
    }
}




