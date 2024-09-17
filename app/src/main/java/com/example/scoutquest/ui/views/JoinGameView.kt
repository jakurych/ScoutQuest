package com.example.scoutquest.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.Browser
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.viewmodels.JoinGameViewModel

@Composable
fun JoinGameView(joinGameViewModel: JoinGameViewModel) {
    val navController = LocalNavigation.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.1f


    val buttonSize = screenWidth * 0.5f

    Column {
        Header()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(elementSpacing))

        TextField(
            value = joinGameViewModel.gameCode,
            onValueChange = { joinGameViewModel.gameCode = it },
            label = { Text("Enter game code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = elementSpacing),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = (screenWidth.value * 0.05f).sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(elementSpacing * 2))

        CircleButton(
            text = "Join game!",
            onClick = { joinGameViewModel.joinGame() },
            modifier = Modifier
                .size(buttonSize)
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircleButton(
            text = "Browse games",
            onClick = { navController.navigate(Browser) },
            modifier = Modifier

        )
    }
}
