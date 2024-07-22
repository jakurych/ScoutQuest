package com.example.scoutquest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scoutquest.ui.views.MainScreenView
import com.example.scoutquest.viewmodels.MainScreenViewModel

@Composable

fun AppNavigation(){
    val navController = rememberNavController()
    val mainScreenViewModel: MainScreenViewModel = viewModel()

    CompositionLocalProvider(LocalNavigation provides navController) {
        NavHost(navController = navController,
            startDestination = MainScreenRoute)
        {
            composable(route= MainScreenRoute){
                MainScreenView(mainScreenViewModel)
            }
        }
    }

}