package com.pocketstudios.feature.editor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.pocketstudios.feature.editor.presentation.EditorUiState
import com.pocketstudios.feature.editor.presentation.EditorViewModel
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: EditorViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = EditorViewModel(
            savedStateHandle = SavedStateHandle(mapOf("projectId" to "test-project-123"))
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is not playing and not loading`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isPlaying)
        assertEquals(0L, state.currentPositionMs)
    }

    @Test
    fun `play updates isPlaying to true`() = runTest {
        viewModel.play()
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `pause updates isPlaying to false`() = runTest {
        viewModel.play()
        viewModel.pause()
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `seekTo updates currentPositionMs`() = runTest {
        viewModel.seekTo(5000L)
        assertEquals(5000L, viewModel.uiState.value.currentPositionMs)
    }

    @Test
    fun `selectClip updates selectedClipId`() = runTest {
        viewModel.selectClip("clip-id-1")
        assertEquals("clip-id-1", viewModel.uiState.value.selectedClipId)
    }

    @Test
    fun `selectClip null clears selection`() = runTest {
        viewModel.selectClip("clip-id-1")
        viewModel.selectClip(null)
        assertNull(viewModel.uiState.value.selectedClipId)
    }

    @Test
    fun `undo is disabled when no history`() = runTest {
        assertFalse(viewModel.uiState.value.canUndo)
    }

    @Test
    fun `timeline scale clamped to valid range`() = runTest {
        // Zoom in extremely
        viewModel.updateTimelineScale(100f)
        assertTrue(viewModel.uiState.value.pixelsPerMs <= 2f)

        // Zoom out extremely
        viewModel.updateTimelineScale(0.0001f)
        assertTrue(viewModel.uiState.value.pixelsPerMs >= 0.02f)
    }

    @Test
    fun `export emits NavigateToExport event`() = runTest {
        viewModel.events.test {
            viewModel.startExport(com.pocketstudios.feature.editor.presentation.ExportSettings(
                resolution = com.pocketstudios.feature.editor.domain.model.Resolution.FHD_1080
            ))
            testDispatcher.scheduler.advanceTimeBy(5000L)
            testDispatcher.scheduler.runCurrent()
            val event = awaitItem()
            assertNotNull(event)
            cancelAndConsumeRemainingEvents()
        }
    }
}
