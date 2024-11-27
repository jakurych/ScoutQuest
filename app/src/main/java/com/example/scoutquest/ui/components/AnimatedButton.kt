package com.example.scoutquest.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.theme.button_green
import kotlinx.coroutines.delay
@Composable
fun AnimatedButton(
    text: String,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(90.dp)
            .scale(scale)
            .clip(shape),
        colors = ButtonDefaults.buttonColors(backgroundColor = button_green)
    ) {
        Text(
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            isHovered = true
            delay(1000)
            isHovered = false
        }
    }
}

