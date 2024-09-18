package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.theme.black_olive
import com.example.scoutquest.ui.theme.detailTextColor
import com.example.scoutquest.ui.theme.eerie_black
import com.example.scoutquest.ui.theme.expandedDetailTextColor
import com.example.scoutquest.ui.theme.moss_green
import com.example.scoutquest.viewmodels.BrowseGamesViewModel
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun UserGamesBrowserView(
    userId: String,
    browseGamesViewModel: BrowseGamesViewModel = hiltViewModel(),
    onEditGame: (Game) -> Unit
) {
    LaunchedEffect(userId) {
        browseGamesViewModel.loadUserGames(userId)
    }

    val games by browseGamesViewModel.filteredGames.collectAsState()
    val searchQuery by browseGamesViewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header()

        // Wyszukiwanie
        BasicTextField(
            value = searchQuery,
            onValueChange = { browseGamesViewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(Color.Gray, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (searchQuery.isEmpty()) {
                        Text("Search games...", color = Color.LightGray)
                    }
                    innerTextField()
                }
            }
        )

        LazyColumn {
            items(games) { game ->
                GameItemWithEdit(game = game, viewModel = browseGamesViewModel, userId = userId, onEditGame = onEditGame)
            }
        }
    }
}


@Composable
fun GameItemWithEdit(game: Game, viewModel: BrowseGamesViewModel, userId: String, onEditGame: (Game) -> Unit) {
    var expanded by rememberSaveable(game.gameId) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var creatorUsername by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(game.creatorId) {
        coroutineScope.launch {
            creatorUsername = viewModel.getCreatorUsername(game.creatorId)
        }
    }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete: ${game.name}?") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.removeGame(game.gameId, userId)
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text(
                        "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Cancel",
                        color = Color.White
                    )
                }
            },
            containerColor = eerie_black
        )
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
                    text = game.name,
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.description,
                fontSize = 14.sp,
                color = detailTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tasks: ${game.tasks.size}",
                fontSize = 14.sp,
                color = detailTextColor
            )
            Text(
                text = "Rating: ${game.rating?.averageRating ?: "No ratings yet"}",
                fontSize = 14.sp,
                color = detailTextColor
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                val cities = viewModel.determineCities(game.tasks).joinToString(", ")
                Text(
                    text = "Cities: $cities",
                    fontSize = 14.sp,
                    color = expandedDetailTextColor
                )
                Text(
                    text = "Distance: ${String.format(Locale.US, "%.2f", viewModel.calculateTotalDistance(game.tasks))} km",
                    fontSize = 14.sp,
                    color = expandedDetailTextColor
                )
                Text(
                    text = "Points: ${game.tasks.sumOf { it.points }}",
                    fontSize = 14.sp,
                    color = expandedDetailTextColor
                )
                Text(
                    text = "Task Types: ${game.tasks.joinToString { it.taskType ?: "Unknown" }}",
                    fontSize = 14.sp,
                    color = expandedDetailTextColor
                )
                Text(
                    text = "Creator: ${creatorUsername ?: "Unknown"}",
                    fontSize = 14.sp,
                    color = expandedDetailTextColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp)
                            .clickable {
                                showDialog = true
                            }
                            .background(Color.Red, shape = MaterialTheme.shapes.small)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Delete", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp)
                            .clickable {
                                onEditGame(game)
                            }
                            .background(Color.Gray, shape = MaterialTheme.shapes.small)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Edit", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
