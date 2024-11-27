package com.example.scoutquest.ui.views.general

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.scoutquest.ui.components.AnimatedButton
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.*

@Composable
fun AdventureGameMenuView() {
    val navController = LocalNavigation.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .weight(0.1f)
            .fillMaxWidth()
        ) {
            Header()
        }

        //idący gościu
        Box(modifier = Modifier
            .weight(0.4f)
            .fillMaxWidth()
            .fillMaxHeight()
        ) {
            val topLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.Url("https://lottie.host/f18a8fab-7a3f-49cd-a5f5-16b6a85033d0/vrIipEnzki.lottie")
            )
            val topLottieProgress by animateLottieCompositionAsState(
                composition = topLottieComposition,
                iterations = LottieConstants.IterateForever,
                speed = 0.8f
            )

            LottieAnimation(
                composition = topLottieComposition,
                progress = { topLottieProgress },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedButton(
                    text = "Browse Games",
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Browser)
                }

                Spacer(modifier = Modifier.width(12.dp))

                AnimatedButton(
                    text = "Continue Game",
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(BrowseSession)
                }
            }
        }

        Row(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxWidth()
                //.fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedButton(
                text = "Create Game",
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(0.55f)
            ) {
                navController.navigate(Creator)
            }

            //kwiatek
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(start = 8.dp)
            ) {
                val bottomLottieComposition by rememberLottieComposition(
                    LottieCompositionSpec.Url("https://lottie.host/e597fae4-36bb-4243-973b-ea2338cf7657/3WfrXpj53N.lottie")
                )
                val bottomLottieProgress by animateLottieCompositionAsState(
                    composition = bottomLottieComposition,
                    iterations = LottieConstants.IterateForever,
                    speed = 0.8f
                )

                LottieAnimation(
                    composition = bottomLottieComposition,
                    progress = { bottomLottieProgress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



