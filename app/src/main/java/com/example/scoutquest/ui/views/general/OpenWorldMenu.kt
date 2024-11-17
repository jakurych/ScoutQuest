package com.example.scoutquest.ui.views.general

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.components.Header
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.scoutquest.data.models.User
import com.example.scoutquest.ui.navigation.AddOpenWorldTask
import com.example.scoutquest.ui.navigation.OpenWorldMap

@Composable
fun OpenWorldMenu(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current

    var openWorldTickets by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                val userData = userDoc.toObject(User::class.java)
                openWorldTickets = userData?.openWorldTicket ?: 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //Header
        Box(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
        ) {
            Header()
        }

        Column(
            modifier = Modifier
                .weight(0.85f)
                .fillMaxWidth()
                .padding(horizontal = screenWidth * 0.05f),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val jumpComposition by rememberLottieComposition(
                LottieCompositionSpec.Url("https://lottie.host/e2963aac-7dca-47f1-af06-fa5765f73dc5/9raN7T8Lzm.json")
            )
            val jumpProgress by animateLottieCompositionAsState(
                composition = jumpComposition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                composition = jumpComposition,
                progress = { jumpProgress },
                modifier = Modifier.size(250.dp)
            )

            CircleButton(
                text = "Jump into open world",
                onClick = {
                    navController.navigate(OpenWorldMap)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(25.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleButton(
                    text = "Create task",
                    onClick = {
                        navController.navigate(AddOpenWorldTask)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(30))
                )

                val createTaskComposition by rememberLottieComposition(
                    LottieCompositionSpec.Url("https://lottie.host/c6e04279-2558-4ec7-85f1-ff26b66c962b/Bf4SCnd9zK.json")
                )
                val createTaskProgress by animateLottieCompositionAsState(
                    composition = createTaskComposition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(
                    composition = createTaskComposition,
                    progress = { createTaskProgress },
                    modifier = Modifier
                        .weight(1f)
                        .size(150.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val infiniteTransition = rememberInfiniteTransition(label = "")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = ""
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .background(Color(0x33FFFFFF))
                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Open world tickets: $openWorldTickets",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
