package com.example.scoutquest.ui.views.general

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.Login
import com.example.scoutquest.ui.navigation.Profile
import com.example.scoutquest.viewmodels.general.UserViewModel

@Composable
fun MainScreenView(viewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

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
                    text = "Play",
                    onClick = { navController.navigate("/joingame") },
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
                    text = "New game",
                    onClick = { navController.navigate("/newgame") },
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
                    text = if (isUserLoggedIn == true) "Profile" else "Log in",
                    onClick = {
                        if (isUserLoggedIn == true) {
                            navController.navigate(Profile)
                        } else {
                            navController.navigate(Login)
                        }
                    },
                    modifier = Modifier
                )
            }


        }
    }
}

