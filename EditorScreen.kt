package com.pocketstudios.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketstudios.core.ui.theme.*
import com.pocketstudios.feature.editor.presentation.EditorEvent
import com.pocketstudios.feature.editor.presentation.EditorPanel
import com.pocketstudios.feature.editor.presentation.EditorViewModel
import com.pocketstudios.feature.editor.presentation.ExportSettings
import com.pocketstudios.feature.editor.ui.EditorToolbar
import com.pocketstudios.feature.editor.ui.FilterPanel
import com.pocketstudios.feature.editor.ui.VideoPreviewPlayer
import com.pocketstudios.feature.editor.ui.VideoTimeline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: String,
    onBack: () -> Unit,
    onExport: (String) -> Unit,
    onProUpgrade: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditorEvent.NavigateToExport -> onExport(event.outputPath)
                is EditorEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is EditorEvent.ShowError -> snackbarHostState.showSnackbar(event.error)
                is EditorEvent.NavigateBack -> onBack()
                is EditorEvent.OpenProUpgrade -> onProUpgrade()
                EditorEvent.ShowSaveConfirmation -> { /* Show dialog */ }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.project?.name ?: "Pocket Studios",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = OnSurface)
                    }
                },
                actions = {
                    // Undo
                    IconButton(
                        onClick = viewModel::undo,
                        enabled = uiState.canUndo
                    ) {
                        Icon(Icons.Default.Undo, "Undo", tint = if (uiState.canUndo) OnSurface else OnSurfaceVariant)
                    }
                    // Redo
                    IconButton(
                        onClick = viewModel::redo,
                        enabled = uiState.canRedo
                    ) {
                        Icon(Icons.Default.Redo, "Redo", tint = if (uiState.canRedo) OnSurface else OnSurfaceVariant)
                    }
                    // Export button
                    Button(
                        onClick = { viewModel.startExport(ExportSettings(com.pocketstudios.feature.editor.domain.model.Resolution.FHD_1080)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary40),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, "Export", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Export")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Surface)
        ) {
            // Zone A: Video Preview (40% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                VideoPreviewPlayer(
                    clips = uiState.videoClips,
                    textOverlays = uiState.textOverlays,
                    currentPositionMs = uiState.currentPositionMs,
                    isPlaying = uiState.isPlaying,
                    aspectRatio = uiState.project?.aspectRatio?.ratio ?: (9f / 16f),
                    onPlayPause = { if (uiState.isPlaying) viewModel.pause() else viewModel.play() }
                )

                // Export progress overlay
                if (uiState.isExporting) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                progress = { uiState.exportProgress },
                                color = Primary80,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Exporting ${(uiState.exportProgress * 100).toInt()}%",
                                color = OnSurface,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // Zone B: Timeline (35% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            viewModel.updateTimelineScale(zoom)
                        }
                    }
            ) {
                VideoTimeline(
                    clips = uiState.videoClips,
                    audioTracks = uiState.audioTracks,
                    textOverlays = uiState.textOverlays,
                    currentPositionMs = uiState.currentPositionMs,
                    durationMs = uiState.durationMs,
                    pixelsPerMs = uiState.pixelsPerMs,
                    selectedClipId = uiState.selectedClipId,
                    onClipSelected = viewModel::selectClip,
                    onSeek = viewModel::seekTo,
                    onSplitAtPosition = { viewModel.splitClipAtCurrentPosition() }
                )
            }

            // Zone C: Tool Panel (25% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .background(SurfaceContainer)
            ) {
                when (uiState.activePanel) {
                    EditorPanel.FILTERS -> FilterPanel(
                        selectedClipId = uiState.selectedClipId,
                        onFilterSelected = viewModel::applyFilter,
                        onDismiss = viewModel::hidePanel
                    )
                    else -> EditorToolbar(
                        hasSelectedClip = uiState.selectedClipId != null,
                        onCut = { viewModel.splitClipAtCurrentPosition() },
                        onFilters = { viewModel.showPanel(EditorPanel.FILTERS) },
                        onText = { viewModel.showPanel(EditorPanel.TEXT) },
                        onAudio = { viewModel.showPanel(EditorPanel.AUDIO) },
                        onSpeed = { viewModel.showPanel(EditorPanel.SPEED) },
                        onColor = { viewModel.showPanel(EditorPanel.COLOR) },
                        onAiCaptions = { viewModel.showPanel(EditorPanel.AI_CAPTIONS) },
                        onTransitions = { viewModel.showPanel(EditorPanel.TRANSITIONS) }
                    )
                }
            }
        }
    }
}
