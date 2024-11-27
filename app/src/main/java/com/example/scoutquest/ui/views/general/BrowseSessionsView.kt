package com.example.scoutquest.ui.views.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.GameMap
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.theme.black_olive
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.detailTextColor
import com.example.scoutquest.ui.theme.moss_green
import com.example.scoutquest.viewmodels.general.BrowseSessionsViewModel

@Composable
fun BrowseSessionsView(
    browseSessionsViewModel: BrowseSessionsViewModel = hiltViewModel(),
    onSessionSelected: ((GameSession) -> Unit)? = null,

) {

    LaunchedEffect(Unit) {
        browseSessionsViewModel.loadActiveSessions()
    }

    val activeSessions by browseSessionsViewModel.activeSessions.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()

        LazyColumn {
            items(activeSessions) { session ->
                SessionItem(
                    session = session,
                    onSessionSelected = { selectedSession ->
                        onSessionSelected?.invoke(selectedSession)
                    },
                    onDeleteSession = { sessionId ->
                        browseSessionsViewModel.deleteSession(sessionId)
                    }
                )
            }
        }
    }
}

@Composable
fun SessionItem(
    session: GameSession,
    browseSessionsViewModel: BrowseSessionsViewModel = hiltViewModel(),
    onSessionSelected: (GameSession) -> Unit,
    onDeleteSession: (String) -> Unit = {}
) {
    val navController = LocalNavigation.current
    var expanded by remember { mutableStateOf(false) }

    var gameData by remember { mutableStateOf<BrowseSessionsViewModel.GameData?>(null) }

    val totalPoints = remember(session) {
        session.scores.values.sum()
    }

    LaunchedEffect(session.gameId) {
        try {
            val data = browseSessionsViewModel.getGameData(session.gameId)

            if (data == null) {
                onDeleteSession(session.sessionId)
            } else {
                gameData = data
            }
        } catch (e: Exception) {
            println("Error checking game: ${e.message}")
            onDeleteSession(session.sessionId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = black_olive)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gameData?.name ?: "Unknown Game",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = moss_green
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = moss_green
                )
            }

            //opis
            gameData?.let { data ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.description,
                    fontSize = 14.sp,
                    color = detailTextColor
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Current Task ${session.currentTaskIndex + 1}",
                    fontSize = 14.sp,
                    color = detailTextColor
                )
                Text(
                    text = "Already Scored Points: $totalPoints",
                    fontSize = 14.sp,
                    color = detailTextColor
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onDeleteSession(session.sessionId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete Session", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onSessionSelected(session)
                            navController.navigate(GameMap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = button_green)
                    ) {
                        Text("Continue!", color = Color.White)
                    }
                }
            }
        }
    }
}





