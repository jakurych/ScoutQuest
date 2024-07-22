package com.example.scoutquest.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoutquest.R
import com.example.scoutquest.viewmodels.MainScreenViewModel

@Composable
fun MainScreenView(viewModel: MainScreenViewModel) {
    Text(text = "Hello")

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF3A3A3A))
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Scout Quest",
                fontSize = 32.sp,
                color = Color.White // Placeholder color
            )
            /*Image(
                painter = painterResource(id = R.drawable.logo), // Placeholder resource ID
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
            )*/
        }

        // Join Game button
        CircleButton(text = "Join game", onClick = { /* Navigate to join game screen */ })

        // New Game button
        CircleButton(text = "New game", onClick = { /* Navigate to new game screen */ })

        // Profile button
        CircleButton(text = "Profile", onClick = { /* Navigate to profile screen */ })

        // Settings button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            /*IconButton(onClick = { *//* Open settings *//* }) {
               *//* Icon(
                    painter = painterResource(id = R.drawable.ic_settings), // Placeholder resource ID
                    contentDescription = "Settings",
                    tint = Color.White // Placeholder color
                )*//*
            }*/
        }
    }
}


@Composable
fun CircleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF556B2F)
        ),
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
    ) {
        Text(text = text, color = Color.White, fontSize = 18.sp)
    }
}


