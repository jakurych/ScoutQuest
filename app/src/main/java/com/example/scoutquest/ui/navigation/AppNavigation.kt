package com.example.scoutquest.ui.navigation

import LoginView
import RegisterView
import RegisterViewModel
import com.example.scoutquest.ui.views.CreateNewGameView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scoutquest.ui.views.*
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.example.scoutquest.viewmodels.LoginViewModel
import com.example.scoutquest.viewmodels.MainScreenViewModel
import com.example.scoutquest.viewmodels.ProfileViewModel
import com.example.scoutquest.viewmodels.SettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val createNewGameViewModel: CreateNewGameViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    CompositionLocalProvider(LocalNavigation provides navController) {
        NavHost(navController = navController, startDestination = MainScreenRoute) {
            composable(route = MainScreenRoute) {
                MainScreenView(mainScreenViewModel)
            }
            composable(route = Profile) {
                ProfileView(profileViewModel)
            }
            composable(route = Settings) {
                SettingsView(settingsViewModel)
            }
            composable(route = NewGame) {
                NewGameView()
            }
            composable(route = Creator) {
                CreateNewGameView(createNewGameViewModel)
            }
            composable(route = Login) {
                LoginView(loginViewModel)
            }
            composable(route = Register) {
                RegisterView(registerViewModel)
            }
        }
    }
}
