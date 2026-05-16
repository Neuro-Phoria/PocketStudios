package com.pocketstudios.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pocketstudios.domain.viewmodel.EditorViewModel

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Editor UI implementation
    }
}