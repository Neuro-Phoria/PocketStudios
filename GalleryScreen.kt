package com.pocketstudios.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.*
import com.pocketstudios.feature.editor.domain.model.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onProjectClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    // TODO: Wire to GalleryViewModel + Hilt
    val projects = remember { emptyList<Project>() }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pocket Studios",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary80
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings", tint = OnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Create new project */ },
                containerColor = Primary40,
                contentColor = Primary90,
                icon = { Icon(Icons.Default.Add, "New Project") },
                text = { Text("New Project") }
            )
        }
    ) { paddingValues ->
        if (projects.isEmpty()) {
            EmptyGalleryState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(
                        project = project,
                        onClick = { onProjectClick(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGalleryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.VideoFile,
            contentDescription = null,
            tint = Primary80.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No projects yet",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap + New Project to start creating",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick)
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .background(SurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            // TODO: AsyncImage with Coil using project.thumbnailPath
            Icon(Icons.Default.VideoFile, null, tint = Primary80.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))

            // Duration badge
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp),
                color = Surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = project.durationMs.toReadableDuration(),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Name + date
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.labelLarge,
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = project.aspectRatio.label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
    }
}

private fun Long.toReadableDuration(): String {
    val totalSecs = this / 1000
    val m = totalSecs / 60
    val s = totalSecs % 60
    return "%d:%02d".format(m, s)
}
