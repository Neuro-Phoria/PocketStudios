package com.pocketstudios.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PocketStudiosNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { 
            // Splash screen
        }
        composable("editor") {
            // Editor screen
        }
        composable("gallery") {
            // Gallery screen
        }
    }
}