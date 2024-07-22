package com.example.scoutquest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scoutquest.R
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.Profile
import com.example.scoutquest.ui.navigation.Settings

@Composable
fun Header() {
    val navController = LocalNavigation.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Icon Button
        IconButton(onClick = { navController.navigate(Profile) }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_user), // Zastąp właściwym zasobem ikony
                contentDescription = "Profile",
                tint = Color.White
            )
        }

        // Title Text
        Text(
            text = "Scout Quest",
            fontSize = 32.sp,
            color = Color.White
        )

        // Settings Icon Button
        IconButton(onClick = { navController.navigate(Settings) }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_setting), // Zastąp właściwym zasobem ikony
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}
