package com.example.scoutquest.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigation = compositionLocalOf<NavHostController> {
    error("NavHostController not provided")
}