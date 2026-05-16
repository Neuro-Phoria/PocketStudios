package com.pocketstudios.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: (isFirstLaunch: Boolean) -> Unit
) {
    // TODO: Check DataStore for first launch flag
    val isFirstLaunch = true

    LaunchedEffect(Unit) {
        delay(1800L)
        onSplashComplete(isFirstLaunch)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary20, Surface)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // TODO: Replace with animated Lottie logo
            Text(
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Pocket Studios",
                style = MaterialTheme.typography.displayMedium,
                color = Primary80
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Your pocket-sized studio",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        }
    }
}
