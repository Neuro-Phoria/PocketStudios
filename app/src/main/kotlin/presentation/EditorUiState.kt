package com.pocketstudios.presentation

sealed class EditorUiState {
    object Loading : EditorUiState()
    data class Success(val data: String) : EditorUiState()
    data class Error(val message: String) : EditorUiState()
}