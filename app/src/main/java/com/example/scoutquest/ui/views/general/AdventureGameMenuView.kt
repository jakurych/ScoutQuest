package com.example.scoutquest.ui.views.general

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.Browser
import com.example.scoutquest.ui.navigation.Creator
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.Login
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.google.firebase.auth.FirebaseAuth
import com.example.scoutquest.ui.theme.bistre

@Composable
fun AdventureGameMenuView() {
    val navController = LocalNavigation.current
    val auth = FirebaseAuth.getInstance()

    var showLoginDialog by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementSpacing),
                horizontalArrangement = Arrangement.Start
            ) {
                CircleButton(
                    text = "Create new game",
                    onClick = {
                        val user = auth.currentUser
                        if (user != null) {
                            if (user.isEmailVerified) {
                                navController.navigate(Creator)
                            } else {
                                showVerificationDialog = true
                            }
                        } else {
                            showLoginDialog = true
                        }
                    },
                    modifier = Modifier
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementSpacing),
                horizontalArrangement = Arrangement.End
            ) {
                CircleButton(
                    text = "<---",
                    onClick = { navController.navigate(MainScreenRoute) },
                    modifier = Modifier
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementSpacing),
                horizontalArrangement = Arrangement.Start
            ) {
                CircleButton(
                    text = "Browse games",
                    onClick = { navController.navigate(Browser) },
                    modifier = Modifier
                )
            }
        }

        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                title = { Text("Login Required") },
                text = { Text("To create a new game, login is required.") },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircleButton(
                            text = "Login",
                            onClick = {
                                navController.navigate(Login)
                                showLoginDialog = false
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                dismissButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircleButton(
                            text = "Cancel",
                            onClick = { showLoginDialog = false },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                containerColor = bistre
            )
        }

        if (showVerificationDialog) {
            AlertDialog(
                onDismissRequest = { showVerificationDialog = false },
                title = { Text("Email Verification Required") },
                text = { Text("Please verify your email address to create a new game.") },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircleButton(
                            text = "Okay",
                            onClick = { showVerificationDialog = false },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                containerColor = bistre
            )
        }
    }
}
