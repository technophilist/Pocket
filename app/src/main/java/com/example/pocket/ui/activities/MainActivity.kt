package com.example.pocket.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocket.auth.AuthenticationService
import com.example.pocket.data.Repository
import com.example.pocket.data.preferences.PocketPreferences
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.example.pocket.ui.screens.HomeScreen
import com.example.pocket.ui.screens.LoginScreen
import com.example.pocket.ui.screens.SignUpScreen
import com.example.pocket.ui.screens.WelcomeScreen
import com.example.pocket.ui.theme.PocketAppTheme
import com.example.pocket.viewmodels.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val isDarkModeSupported =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var authenticationService: AuthenticationService

    private val mainActivityViewModel: MainActivityViewModel by viewModels<MainActivityViewModelImpl>()

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(isDarkModeSupported)
        setContent {
            PocketAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PocketApp()
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @Composable
    private fun PocketApp() {
        val navController = rememberAnimatedNavController()
        val slideAnimationSpec = tween<IntOffset>(350)
        val fadeAnimationSpec = tween<Float>(350)

        AnimatedNavHost(
            navController = navController,
            startDestination = if (authenticationService.isLoggedIn) PocketNavigationDestinations.HOME_SCREEN
            else PocketNavigationDestinations.WELCOME_SCREEN,
            enterTransition = {
                when (targetState.destination.route) {
                    // use this animation when the user successfully logs in
                    PocketNavigationDestinations.HOME_SCREEN -> fadeIn(animationSpec = fadeAnimationSpec)
                    // use this animation when the user logs out and returns to the welcome screen
                    PocketNavigationDestinations.WELCOME_SCREEN -> fadeIn(animationSpec = fadeAnimationSpec)
                    else -> fadeIn(animationSpec = fadeAnimationSpec) + slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = slideAnimationSpec
                    )
                }
            },
            exitTransition = { fadeOut(animationSpec = fadeAnimationSpec) },
            popExitTransition = {
                fadeOut(animationSpec = fadeAnimationSpec) + slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = slideAnimationSpec
                )
            },
            popEnterTransition = { fadeIn(animationSpec = fadeAnimationSpec) },
        ) {
            composable(PocketNavigationDestinations.WELCOME_SCREEN) {
                WelcomeScreen(navController = navController)
            }

            composable(PocketNavigationDestinations.LOGIN_SCREEN) { backstackEntry ->
                val loginViewModel = hiltViewModel<LoginViewModelImpl>(backstackEntry)
                LoginScreen(loginViewModel, navController)
            }

            composable(PocketNavigationDestinations.SIGNUP_SCREEN) { backstackEntry ->
                val signUpViewModel = hiltViewModel<SignUpViewModelImpl>(backstackEntry)
                SignUpScreen(signUpViewModel, navController)
            }

            composable(PocketNavigationDestinations.HOME_SCREEN) { backStackEntry ->
                val isDarkModeSupported = remember { isDarkModeSupported }
                val appTheme by mainActivityViewModel.currentAppTheme.observeAsState()
                val homeScreenViewModel = hiltViewModel<HomeScreenViewModelImpl>(backStackEntry)
                val coroutineScope = rememberCoroutineScope()
                /*
                if the system supports dark mode, use the system's current theme,else
                observe for changes in the appTheme from the viewModel
                */
                val isDarkModeEnabled =
                    if (isDarkModeSupported) isSystemInDarkTheme() else (appTheme == PocketPreferences.AppTheme.DARK)
                HomeScreen(
                    homeScreenViewModel = homeScreenViewModel,
                    navController = navController,
                    onClickUrlItem = { openUrl(it.url) },
                    isDarkModeSupported = isDarkModeSupported,
                    onDarkModeOptionClicked = {
                        mainActivityViewModel.changeAppTheme(
                            if (appTheme == PocketPreferences.AppTheme.LIGHT) PocketPreferences.AppTheme.DARK
                            else PocketPreferences.AppTheme.LIGHT
                        )
                    },
                    onSignOutButtonClick = {
                        coroutineScope.launch(Dispatchers.IO) { authenticationService.signOut() }
                    },
                    isDarkModeEnabled = isDarkModeEnabled
                )
            }
        }
    }

    private fun setStatusBarColor(isDarkModeSupported: Boolean) {
        /*
        if dark mode is not supported , then the app theme will not automatically
        change to reflect the night mode colors defined for the status bar in
        the xml file
        */
        if (!isDarkModeSupported) {
            // observing the current app theme to set the correct status bar color
            mainActivityViewModel.currentAppTheme.observe(this) { theme ->
                AppCompatDelegate.setDefaultNightMode(
                    if (theme == PocketPreferences.AppTheme.DARK) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
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




