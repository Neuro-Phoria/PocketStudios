package com.pocketstudios.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.pocketstudios.PocketStudiosApp
import com.pocketstudios.ui.theme.PocketStudiosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketStudiosTheme {
                Surface {
                    PocketStudiosApp()
                }
            }
        }
    }
}