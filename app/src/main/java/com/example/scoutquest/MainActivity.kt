package com.example.scoutquest
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.scoutquest.ui.navigation.AppNavigation
import com.example.scoutquest.ui.theme.ScoutQuestTheme
import com.example.scoutquest.viewmodels.CreateNewGameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CreateNewGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutQuestTheme {
                    AppNavigation()

            }
        }

        // Przykładowy obiekt Location
        val initialLocation = Location("provider").apply {
            latitude = 52.253126
            longitude = 20.900157

        }

        // Przekazanie initialLocation do ViewModel
        viewModel.onLocationSelected(initialLocation)
    }
}
