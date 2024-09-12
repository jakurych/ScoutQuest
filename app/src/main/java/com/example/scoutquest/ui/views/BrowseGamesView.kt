package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.ui.theme.*
import com.example.scoutquest.viewmodels.BrowseGamesViewModel

@Composable
fun BrowseGamesView(
    navController: NavController,
    browseGamesViewModel: BrowseGamesViewModel = hiltViewModel()
) {
    val games by browseGamesViewModel.games.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(eerie_black)
    ) {
        Header()
        LazyColumn {
            items(games) { game ->
                GameItem(game)
            }
        }
    }
}

@Composable
fun GameItem(game: Game) {
    var expanded by remember { mutableStateOf(false) }

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
                color = drab_dark_brown
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tasks: ${game.tasks.size}",
                fontSize = 14.sp,
                color = drab_dark_brown
            )
            Text(
                text = "Rating: ${game.rating?.averageRating ?: "No ratings yet"}",
                fontSize = 14.sp,
                color = drab_dark_brown
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Created by: ${game.creatorEmail}",
                    fontSize = 12.sp,
                    color = bistre
                )
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bistre)
            .padding(16.dp)
    ) {
        Text(
            text = "Browse Games",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = moss_green
        )
    }
}
