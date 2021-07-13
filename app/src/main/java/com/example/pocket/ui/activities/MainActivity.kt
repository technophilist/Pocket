package com.example.pocket.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocket.auth.FirebaseAuthenticationService
import com.example.pocket.data.preferences.PocketPreferences
import com.example.pocket.di.AppContainer
import com.example.pocket.di.LoginContainer
import com.example.pocket.di.PocketApplication
import com.example.pocket.ui.navigation.NavigationDestinations
import com.example.pocket.ui.screens.HomeScreen
import com.example.pocket.ui.screens.LoginScreen
import com.example.pocket.ui.screens.SignUpScreen
import com.example.pocket.ui.screens.WelcomeScreen
import com.example.pocket.ui.theme.PocketAppTheme
import com.example.pocket.utils.HomeScreenViewModelFactory
import com.example.pocket.viewmodels.HomeScreenViewModel
import com.example.pocket.viewmodels.HomeScreenViewModelImpl
import com.example.pocket.viewmodels.LoginViewModelImpl
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : AppCompatActivity() {
    private val isDarkModeSupported =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private lateinit var mViewModel: HomeScreenViewModel
    private lateinit var appContainer: AppContainer

    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = (application as PocketApplication).appContainer
        mViewModel = ViewModelProvider(
            this,
            HomeScreenViewModelFactory(application, appContainer.pocketRepository)
        ).get(HomeScreenViewModelImpl::class.java)

        /*
         if dark mode is not supported , then the app theme will not automatically
         change to reflect the night mode colors defined for the status bar in
         the xml file
         */
        if (!isDarkModeSupported) {
            // observing the current app theme to set the correct status bar color
            mViewModel.currentAppTheme.observe(this) { theme ->
                AppCompatDelegate.setDefaultNightMode(
                    if (theme == PocketPreferences.AppTheme.DARK) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        setContent {
            PocketAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PocketApp()
                }
            }
        }
    }

    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @Composable
    private fun PocketApp() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = NavigationDestinations.WELCOME_SCREEN.navigationString
        ) {
            composable(NavigationDestinations.WELCOME_SCREEN.navigationString) {
                WelcomeScreen(navController = navController)
            }

            composable(NavigationDestinations.LOGIN_SCREEN.navigationString) {
                LoginScreen(appContainer,navController)
            }

            composable(NavigationDestinations.SIGNUP_SCREEN.navigationString) {
                SignUpScreen(appContainer,navController)
            }

            composable(NavigationDestinations.HOME_SCREEN.navigationString) {
                val isDarkModeSupported = remember { isDarkModeSupported }
                val appTheme by mViewModel.currentAppTheme.observeAsState()
                val isDarkModeEnabled = if (isDarkModeSupported) {
                    /*
                     if the system supports dark mode, use the system's current theme,else
                     observe for changes in the appTheme from the viewModel
                     */
                    isSystemInDarkTheme()
                } else {
                    (appTheme == PocketPreferences.AppTheme.DARK)
                }

                HomeScreen(
                    viewModel = mViewModel,
                    onClickUrlItem = { openUrl(it.url) },
                    isDarkModeSupported = isDarkModeSupported,
                    onDarkModeIconClicked = {
                        mViewModel.changeAppTheme(
                            if (appTheme == PocketPreferences.AppTheme.LIGHT) PocketPreferences.AppTheme.DARK
                            else PocketPreferences.AppTheme.LIGHT
                        )
                    },
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




