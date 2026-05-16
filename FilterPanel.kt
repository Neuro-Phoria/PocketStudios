package com.pocketstudios.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.*

data class FilterPreset(
    val id: String,
    val name: String,
    val isPro: Boolean = false
)

val FREE_FILTERS = listOf(
    FilterPreset("natural", "Natural"),
    FilterPreset("cinema_01", "Cinema"),
    FilterPreset("vintage_warm", "Vintage"),
    FilterPreset("chrome", "Chrome"),
    FilterPreset("fade", "Fade"),
    FilterPreset("vivid", "Vivid"),
    FilterPreset("matte", "Matte"),
    FilterPreset("noir", "Noir"),
    FilterPreset("golden_hour", "Golden Hour"),
    FilterPreset("cool_blue", "Cool Blue"),
    FilterPreset("moody", "Moody"),
    FilterPreset("pastel", "Pastel"),
    FilterPreset("warm_sunset", "Sunset"),
    FilterPreset("arctic", "Arctic"),
    FilterPreset("coffee", "Coffee"),
    FilterPreset("emerald", "Emerald"),
    FilterPreset("rose", "Rose"),
    FilterPreset("slate", "Slate"),
    FilterPreset("amber", "Amber"),
    FilterPreset("teal_orange", "Teal + Orange")
)

val PRO_FILTERS = listOf(
    FilterPreset("film_35mm", "35mm Film", isPro = true),
    FilterPreset("lut_moody", "Moody LUT", isPro = true),
    FilterPreset("lut_cinematic", "Cinematic LUT", isPro = true),
    FilterPreset("film_grain", "Film Grain", isPro = true),
    FilterPreset("bleach_bypass", "Bleach Bypass", isPro = true)
)

@Composable
fun FilterPanel(
    selectedClipId: String?,
    onFilterSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Free", "Pro")
    val filters = if (selectedTab == 0) FREE_FILTERS else FREE_FILTERS + PRO_FILTERS

    Column(modifier = modifier.fillMaxSize().background(SurfaceContainer)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filters", style = MaterialTheme.typography.titleMedium, color = OnSurface)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "Close", tint = OnSurfaceVariant)
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceContainer,
            contentColor = Primary80
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tab) }
                )
            }
        }

        // Filter grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters, key = { it.id }) { filter ->
                FilterItem(
                    filter = filter,
                    onClick = {
                        if (!filter.isPro) onFilterSelected(filter.id)
                        // TODO: else show pro upgrade prompt
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterItem(
    filter: FilterPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .background(ClipVideoColor, RoundedCornerShape(8.dp))
                .border(1.dp, if (filter.isPro) ProGoldStart else Color.Transparent, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Animated filter preview using Coil + shader
            if (filter.isPro) {
                Icon(Icons.Default.Lock, "Pro", tint = ProGoldStart, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = filter.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (filter.isPro) ProGoldStart else OnSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
