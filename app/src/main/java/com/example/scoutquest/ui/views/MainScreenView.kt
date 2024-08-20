package com.example.scoutquest.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.viewmodels.MainScreenViewModel

@Composable
fun MainScreenView(viewModel: MainScreenViewModel) {
    val navController = LocalNavigation.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding) //  dynamic padding
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            // Join Game button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementSpacing),
                horizontalArrangement = Arrangement.Start
            ) {
                CircleButton(
                    text = "Join game",
                    onClick = { navController.navigate("/joingame") },
                    modifier = Modifier
                )
            }

            // New Game button
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

            // Profile button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementSpacing),
                horizontalArrangement = Arrangement.Start
            ) {
                CircleButton(
                    text = "Profile",
                    onClick = { navController.navigate("/profile") },
                    modifier = Modifier
                )
            }
        }
    }
}
