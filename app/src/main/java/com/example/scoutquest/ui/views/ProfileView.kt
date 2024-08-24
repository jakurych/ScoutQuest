package com.example.scoutquest.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.Login
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.viewmodels.ProfileViewModel
import com.example.scoutquest.viewmodels.UserViewModel

@Composable
fun ProfileView(profileViewModel: ProfileViewModel, userViewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by userViewModel.isUserLoggedIn.collectAsState()
    val userEmail by profileViewModel.userEmail.collectAsState()

    if (isUserLoggedIn == false) {
        LaunchedEffect(Unit) {
            navController.navigate(MainScreenRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to your profile")
        userEmail?.let {
            Text("Email: $it")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* change password functionality */ }) {
            Text("Change Password")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            userViewModel.logout()
            navController.navigate(MainScreenRoute) {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}

