package com.example.scoutquest.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.theme.*
import com.example.scoutquest.viewmodels.BrowseGamesViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun BrowseGamesView(
    browseGamesViewModel: BrowseGamesViewModel = hiltViewModel()
) {
    val games by browseGamesViewModel.filteredGames.collectAsState()
    val allGames by browseGamesViewModel.games.collectAsState()

    val allCities = remember(allGames) {
        allGames.flatMap { browseGamesViewModel.determineCities(it.tasks) }.distinct()
    }

    var selectedCities by remember { mutableStateOf(setOf<String>()) }
    var minDistance by remember { mutableStateOf<Double?>(null) }
    var maxDistance by remember { mutableStateOf<Double?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header()
        Button(
            onClick = { showFilters = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = button_green)
        ) {
            Text("Filters")
        }
        if (showFilters) {
            FilterDialog(
                allCities = allCities,
                selectedCities = selectedCities,
                onCitiesSelected = { selectedCities = it },
                minDistance = minDistance,
                maxDistance = maxDistance,
                onMinDistanceChanged = { minDistance = it },
                onMaxDistanceChanged = { maxDistance = it },
                onDismissRequest = { showFilters = false },
                onApplyFilters = {
                    browseGamesViewModel.updateCityFilter(selectedCities)
                    browseGamesViewModel.updateDistanceFilter(minDistance, maxDistance)
                    showFilters = false
                },
                onResetFilters = {
                    selectedCities = setOf()
                    minDistance = null
                    maxDistance = null
                    browseGamesViewModel.updateCityFilter(selectedCities)
                    browseGamesViewModel.updateDistanceFilter(minDistance, maxDistance)
                    showFilters = false
                }
            )
        }
        LazyColumn {
            items(games) { game ->
                GameItem(game, browseGamesViewModel)
            }
        }
    }
}

@Composable
fun FilterDialog(
    allCities: List<String>,
    selectedCities: Set<String>,
    onCitiesSelected: (Set<String>) -> Unit,
    minDistance: Double?,
    maxDistance: Double?,
    onMinDistanceChanged: (Double?) -> Unit,
    onMaxDistanceChanged: (Double?) -> Unit,
    onDismissRequest: () -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Filter Games", fontWeight = FontWeight.Bold, color = moss_green)
        },
        text = {
            Column {
                Text("Filter by Cities", fontWeight = FontWeight.Bold)

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    LazyColumn {
                        items(allCities.filter { it != "Unknown" }) { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newSelection = selectedCities.toMutableSet()
                                        if (newSelection.contains(city)) {
                                            newSelection.remove(city)
                                        } else {
                                            newSelection.add(city)
                                        }
                                        onCitiesSelected(newSelection)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedCities.contains(city),
                                    onCheckedChange = null
                                )
                                Text(city)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Filter by Distance (km)", fontWeight = FontWeight.Bold)

                // minimum
                Text("Min Distance: ${if (minDistance == Double.POSITIVE_INFINITY) "∞" else "${minDistance?.toInt() ?: 0}"} km")
                Slider(
                    value = minDistance?.let {
                        if (it == Double.POSITIVE_INFINITY) 1f
                        else (ln(it + 1) / ln(501.0)).toFloat()
                    } ?: 0f,
                    onValueChange = {
                        val newValue = when {
                            it >= 0.99f -> Double.POSITIVE_INFINITY
                            else -> (500.0.pow(it.toDouble()) - 1).roundToInt().toDouble()
                        }
                        onMinDistanceChanged(newValue)
                    },
                    valueRange = 0f..1f,
                    steps = 100
                )

                //maximum distance
                Text("Max Distance: ${if (maxDistance == Double.POSITIVE_INFINITY) "∞" else "${maxDistance?.toInt() ?: 0}"} km")
                Slider(
                    value = maxDistance?.let {
                        if (it == Double.POSITIVE_INFINITY) 1f
                        else (ln(it + 1) / ln(501.0)).toFloat()
                    } ?: 0f,
                    onValueChange = {
                        val newValue = when {
                            it >= 0.99f -> Double.POSITIVE_INFINITY
                            else -> (500.0.pow(it.toDouble()) - 1).roundToInt().toDouble()
                        }
                        onMaxDistanceChanged(newValue)
                    },
                    valueRange = 0f..1f,
                    steps = 100
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onApplyFilters,
                colors = ButtonDefaults.textButtonColors(containerColor = button_green)
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onResetFilters,
                colors = ButtonDefaults.textButtonColors(containerColor = button_green)
            ) {
                Text("Reset filters")
            }
        },
        containerColor = black_olive
    )
}


@Composable
fun GameItem(game: Game, viewModel: BrowseGamesViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var creatorUsername by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(game.creatorId) {
        coroutineScope.launch {
            creatorUsername = viewModel.getCreatorUsername(game.creatorId)
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
            }
        }
    }
}
