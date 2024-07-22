package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.viewmodels.MainScreenViewModel

@Composable
fun MainScreenView(viewModel: MainScreenViewModel) {
    val navController = LocalNavigation.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3A3A3A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            // Join Game button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                    .padding(20.dp),
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
                    .padding(20.dp),
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
