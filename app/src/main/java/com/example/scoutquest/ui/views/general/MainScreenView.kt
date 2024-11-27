package com.example.scoutquest.ui.views.general

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoutquest.R
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.*
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.viewmodels.general.UserViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@Composable
fun MainScreenView(viewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header (15% of screen)
        Box(modifier = Modifier
            .weight(0.15f)
            .fillMaxWidth()
           // .background(Color.LightGray)
        ) {
            Header()
        }

        // Logo (25% of screen)
        Box(modifier = Modifier
            .weight(0.25f)
            //.background(Color.Yellow.copy(alpha = 0.3f))
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedLogo()
        }

        //Spacer(modifier = Modifier.weight(0.05f))

        // Scrolling Text (30% of screen)
        Box(modifier = Modifier
            .weight(0.30f)
            .fillMaxWidth()
           // .background(Color.Green.copy(alpha = 0.3f))
        ) {
            ScrollingText()
        }

        // Buttons (25% of screen)
        Box(modifier = Modifier
            .weight(0.25f)
            .fillMaxWidth()
            //.background(Color.Blue.copy(alpha = 0.3f))
            .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isUserLoggedIn == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AnimatedButton("Profile", RoundedCornerShape(10.dp)) { navController.navigate(Profile) }
                    AnimatedButton("Guided\nAdventure", RoundedCornerShape(10.dp)) { navController.navigate(NewGame) }
                    AnimatedButton("Open\nWorld", RoundedCornerShape(10.dp)) { navController.navigate(JoinGame) }
                }
            } else {
                AnimatedButton("Log in", shape = RoundedCornerShape(10.dp)) { navController.navigate(Login) }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScrollingText() {
    val texts = listOf(
        "Reduce stress now. 15 minutes of walking can cut cortisol levels by 50%.",
        "Boost your immune system with a daily walk! It's fun and healthy!",
        "Discover new routes! Enhance your sense of direction and spatial awareness!",
        "Walking through green spaces can significantly lift your mood",
        "Sharpen your mind! Regular walks increase blood flow to the brain!",
        "Stay active, stay healthy! Walking is a low-impact exercise suitable for all ages."
    )

    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        while(true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % texts.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        count = texts.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texts[page],
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Cyan.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        painter = painterResource(id = R.drawable.wcy_logo),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxSize(1f)
            .scale(scale)
    )
}

@Composable
fun AnimatedButton(text: String, shape: androidx.compose.ui.graphics.Shape, onClick: () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isHovered) 1.1f else 1f, label = "")

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(90.dp)
            .widthIn(min = 100.dp)
            .scale(scale)
            .clip(shape),
        colors = ButtonDefaults.buttonColors(backgroundColor = button_green)
    ) {
        Text(text = text, color = Color.White)
    }

    LaunchedEffect(Unit) {
        while (true) {
            isHovered = true
            delay(1500)
            isHovered = false
            delay(1500)
        }
    }
}
