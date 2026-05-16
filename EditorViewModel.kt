package com.pocketstudios.feature.editor.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditorEvent>()
    val events: SharedFlow<EditorEvent> = _events.asSharedFlow()

    // Undo/Redo history — sealed EditAction hierarchy
    private val editHistory = mutableListOf<EditAction>()
    private var historyIndex = -1

    init {
        loadProject()
    }

    private fun loadProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Inject and use GetProjectUseCase + GetClipsUseCase
            // Placeholder: project loads from Room via use case
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // ─── Playback ──────────────────────────────────────────────────────────
    fun play() = _uiState.update { it.copy(isPlaying = true) }
    fun pause() = _uiState.update { it.copy(isPlaying = false) }
    fun seekTo(positionMs: Long) = _uiState.update { it.copy(currentPositionMs = positionMs) }

    // ─── Clip Selection ────────────────────────────────────────────────────
    fun selectClip(clipId: String?) {
        _uiState.update { it.copy(selectedClipId = clipId, selectedAudioId = null, selectedOverlayId = null) }
    }

    // ─── Split ─────────────────────────────────────────────────────────────
    fun splitClipAtCurrentPosition() {
        viewModelScope.launch {
            val state = _uiState.value
            val position = state.currentPositionMs
            val clip = state.videoClips.firstOrNull {
                position in it.timelinePositionMs..(it.timelinePositionMs + it.scaledDurationMs)
            } ?: run {
                _events.emit(EditorEvent.ShowSnackbar("No clip at playhead position"))
                return@launch
            }

            // TODO: Call SplitClipUseCase(SplitParams(clip.id, position - clip.timelinePositionMs))
            _events.emit(EditorEvent.ShowSnackbar("Clip split at ${position / 1000}s"))
        }
    }

    // ─── Filter ────────────────────────────────────────────────────────────
    fun applyFilter(filterId: String) {
        viewModelScope.launch {
            val clipId = _uiState.value.selectedClipId ?: return@launch
            _uiState.update { state ->
                state.copy(videoClips = state.videoClips.map { clip ->
                    if (clip.id == clipId) clip.copy(filterPresetId = filterId) else clip
                })
            }
            recordAction(EditAction.ApplyFilter(clipId, filterId))
        }
    }

    // ─── Speed ─────────────────────────────────────────────────────────────
    fun setClipSpeed(clipId: String, speed: Float) {
        _uiState.update { state ->
            state.copy(videoClips = state.videoClips.map { clip ->
                if (clip.id == clipId) clip.copy(speed = speed.coerceIn(0.1f, 10f)) else clip
            })
        }
    }

    // ─── Export ────────────────────────────────────────────────────────────
    fun startExport(settings: ExportSettings) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, exportProgress = 0f) }
            // TODO: Inject and call ExportVideoUseCase — collects ExportProgress flow
            // Simulate progress for now:
            for (i in 1..10) {
                kotlinx.coroutines.delay(300)
                _uiState.update { it.copy(exportProgress = i / 10f) }
            }
            _uiState.update { it.copy(isExporting = false) }
            _events.emit(EditorEvent.NavigateToExport("/storage/exported_video.mp4"))
        }
    }

    // ─── Undo / Redo ────────────────────────────────────────────────────────
    fun undo() {
        if (historyIndex >= 0) {
            viewModelScope.launch { applyReverse(editHistory[historyIndex]) }
            historyIndex--
            _uiState.update { it.copy(canUndo = historyIndex >= 0, canRedo = true) }
        }
    }

    fun redo() {
        if (historyIndex < editHistory.size - 1) {
            historyIndex++
            viewModelScope.launch { applyForward(editHistory[historyIndex]) }
            _uiState.update { it.copy(canUndo = true, canRedo = historyIndex < editHistory.size - 1) }
        }
    }

    private fun recordAction(action: EditAction) {
        if (historyIndex < editHistory.size - 1) {
            editHistory.subList(historyIndex + 1, editHistory.size).clear()
        }
        editHistory.add(action)
        historyIndex++
        if (editHistory.size > 50) { editHistory.removeAt(0); historyIndex-- }
        _uiState.update { it.copy(canUndo = true, canRedo = false) }
    }

    private suspend fun applyReverse(action: EditAction) {
        // TODO: Implement reverse for each action type
    }

    private suspend fun applyForward(action: EditAction) {
        // TODO: Implement forward re-apply for each action type
    }

    // ─── Panel ─────────────────────────────────────────────────────────────
    fun showPanel(panel: EditorPanel) = _uiState.update { it.copy(activePanel = panel) }
    fun hidePanel() = _uiState.update { it.copy(activePanel = EditorPanel.NONE) }

    // ─── Timeline Scale ─────────────────────────────────────────────────────
    fun updateTimelineScale(zoom: Float) {
        _uiState.update { state ->
            state.copy(pixelsPerMs = (state.pixelsPerMs * zoom).coerceIn(0.02f, 2f))
        }
    }
}

// Edit action history
sealed class EditAction {
    data class ApplyFilter(val clipId: String, val filterId: String) : EditAction()
    data class SetSpeed(val clipId: String, val oldSpeed: Float, val newSpeed: Float) : EditAction()
    data class SplitClip(val originalId: String, val newClipId: String, val splitAtMs: Long) : EditAction()
    data class DeleteClip(val clipId: String) : EditAction()
    data class MoveClip(val clipId: String, val fromMs: Long, val toMs: Long) : EditAction()
    data class TrimClip(val clipId: String, val oldStart: Long, val oldEnd: Long, val newStart: Long, val newEnd: Long) : EditAction()
    data class AddTextOverlay(val overlayId: String) : EditAction()
    data class DeleteTextOverlay(val overlayId: String) : EditAction()
}
