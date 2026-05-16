package com.pocketstudios.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.TimelineBackground
import com.pocketstudios.feature.editor.domain.model.AudioTrack
import com.pocketstudios.feature.editor.domain.model.TextOverlay
import com.pocketstudios.feature.editor.domain.model.VideoClip

@Composable
fun VideoTimeline(
    clips: List<VideoClip>,
    audioTracks: List<AudioTrack>,
    textOverlays: List<TextOverlay>,
    currentPositionMs: Long,
    durationMs: Long,
    pixelsPerMs: Float,
    selectedClipId: String?,
    onClipSelected: (String?) -> Unit,
    onSeek: (Long) -> Unit,
    onSplitAtPosition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TimelineBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            // Timecode ruler
            TimelineRuler(
                durationMs = durationMs,
                pixelsPerMs = pixelsPerMs,
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Video tracks
            val videoTracks = clips.groupBy { it.trackIndex }
            videoTracks.forEach { (_, trackClips) ->
                VideoTrackLane(
                    clips = trackClips,
                    pixelsPerMs = pixelsPerMs,
                    selectedClipId = selectedClipId,
                    onClipSelected = onClipSelected
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Audio tracks
            audioTracks.forEach { track ->
                AudioTrackLane(
                    track = track,
                    pixelsPerMs = pixelsPerMs
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Text overlay track
            if (textOverlays.isNotEmpty()) {
                TextOverlayTrackLane(
                    overlays = textOverlays,
                    pixelsPerMs = pixelsPerMs
                )
            }
        }

        // Playhead
        PlayheadIndicator(
            positionMs = currentPositionMs,
            durationMs = durationMs,
            pixelsPerMs = pixelsPerMs
        )
    }
}
