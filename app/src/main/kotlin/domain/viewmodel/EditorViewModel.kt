package com.pocketstudios.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketstudios.presentation.EditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EditorUiState>(EditorUiState.Loading)
    val uiState: StateFlow<EditorUiState> = _uiState
    
    init {
        loadEditor()
    }
    
    private fun loadEditor() {
        viewModelScope.launch {
            // Load editor state
        }
    }
}