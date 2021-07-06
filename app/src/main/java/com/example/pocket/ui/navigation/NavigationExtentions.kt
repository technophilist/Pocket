package com.example.pocket.ui.navigation

import androidx.navigation.NavController

fun NavController.navigate(
    navigationDestination: NavigationDestinations
) = navigate(navigationDestination.navigationString)



