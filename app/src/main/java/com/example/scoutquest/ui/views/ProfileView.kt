package com.example.scoutquest.ui.views
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.scoutquest.ui.navigation.Login
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.viewmodels.ProfileViewModel

@Composable
fun ProfileView(profileViewModel: ProfileViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by profileViewModel.isUserLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.checkUserLoggedIn()
        if (!isUserLoggedIn) {
            navController.navigate(Login) {
                popUpTo(MainScreenRoute) { inclusive = false }
            }
        }
    }

    if (isUserLoggedIn) {
        Text("Welcome to your profile")
        // Dodaj inne elementy interfejsu profilu
    }
}
