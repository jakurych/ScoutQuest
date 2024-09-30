package com.example.scoutquest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.scoutquest.ui.navigation.AppNavigation
import com.example.scoutquest.ui.theme.ScoutQuestTheme
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CreateNewGameViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onLocationPermissionGranted()
        } else {
            // Obsłuż przypadek, gdy uprawnienie zostało odmówione
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutQuestTheme {
                AppNavigation()
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            onLocationPermissionGranted()
        }
    }

    private fun onLocationPermissionGranted() {
        val initialLocation = Location("provider").apply {
            latitude = 52.253126
            longitude = 20.900157
        }
        viewModel.onLocationSelected(initialLocation.latitude, initialLocation.longitude)
    }
}
