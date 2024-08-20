package com.example.scoutquest.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoutquest.ui.theme.button_green

@Composable
fun CircleButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val buttonSize = screenWidth * 0.4f

    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = button_green
        ),
        modifier = modifier
            .size(buttonSize)
            .clip(CircleShape)
    ) {
        Text(text = text, color = Color.White, fontSize = (buttonSize.value * 0.1f).sp)
    }
}
