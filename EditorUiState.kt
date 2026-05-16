package com.pocketstudios.feature.editor.presentation

import com.pocketstudios.feature.editor.domain.model.AudioTrack
import com.pocketstudios.feature.editor.domain.model.Project
import com.pocketstudios.feature.editor.domain.model.TextOverlay
import com.pocketstudios.feature.editor.domain.model.VideoClip

data class EditorUiState(
    val project: Project? = null,
    val videoClips: List<VideoClip> = emptyList(),
    val audioTracks: List<AudioTrack> = emptyList(),
    val textOverlays: List<TextOverlay> = emptyList(),
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isPlaying: Boolean = false,
    val selectedClipId: String? = null,
    val selectedAudioId: String? = null,
    val selectedOverlayId: String? = null,
    val activePanel: EditorPanel = EditorPanel.NONE,
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val pixelsPerMs: Float = 0.1f,
    val error: String? = null
)

enum class EditorPanel {
    NONE, TRIM, SPLIT, FILTERS, TRANSITIONS, TEXT, AUDIO, SPEED, COLOR, AI_CAPTIONS, STICKERS
}

sealed class EditorEvent {
    data class NavigateToExport(val outputPath: String) : EditorEvent()
    data class ShowSnackbar(val message: String) : EditorEvent()
    object ShowSaveConfirmation : EditorEvent()
    data class ShowError(val error: String) : EditorEvent()
    object NavigateBack : EditorEvent()
    object OpenProUpgrade : EditorEvent()
}

data class ExportSettings(
    val resolution: com.pocketstudios.feature.editor.domain.model.Resolution,
    val frameRate: Int = 30,
    val bitrateMbps: Int = 8,
    val format: ExportFormat = ExportFormat.MP4,
    val includeWatermark: Boolean = false
)

enum class ExportFormat { MP4, WEBM }
