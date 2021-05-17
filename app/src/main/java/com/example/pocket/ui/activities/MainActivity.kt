package com.example.pocket.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModelProvider
import com.example.pocket.data.PocketRepository
import com.example.pocket.data.Repository
import com.example.pocket.ui.screens.HomeScreen
import com.example.pocket.ui.theme.PocketAppTheme
import com.example.pocket.utils.HomeScreenViewModelFactory
import com.example.pocket.viewmodels.HomeScreenViewModel
import com.example.pocket.viewmodels.HomeScreenViewModelImpl


class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: HomeScreenViewModel
    private lateinit var mRepository: Repository

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRepository = PocketRepository.getInstance(applicationContext)

        mViewModel = ViewModelProvider(
            this,
            HomeScreenViewModelFactory(application,mRepository)
        ).get(HomeScreenViewModelImpl::class.java)

        setContent {
            PocketAppTheme {
                HomeScreen(viewModel = mViewModel, onClickUrlItem = { openUrl(it.url) })
            }
        }

    }

    private fun openUrl(urlString: String) {
        val uri = Uri.parse(urlString)
        val openLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(openLinkIntent)
    }
}




