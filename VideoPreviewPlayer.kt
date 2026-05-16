package com.pocketstudios.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pocketstudios.feature.editor.domain.model.TextOverlay
import com.pocketstudios.feature.editor.domain.model.VideoClip

@Composable
fun VideoPreviewPlayer(
    clips: List<VideoClip>,
    textOverlays: List<TextOverlay>,
    currentPositionMs: Long,
    isPlaying: Boolean,
    aspectRatio: Float,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .background(Color.Black)
            .clickable { onPlayPause() },
        contentAlignment = Alignment.Center
    ) {
        // TODO: Integrate ExoPlayer via AndroidView + PlayerView
        // ExoPlayer renders video frames here

        // Active text overlays rendered on top
        textOverlays
            .filter { currentPositionMs in it.startTimeMs..it.endTimeMs }
            .forEach { overlay ->
                // TODO: TextOverlayRenderer(overlay)
            }

        // Play/Pause indicator (fades after 1s)
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
