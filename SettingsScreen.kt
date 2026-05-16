package com.pocketstudios.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onProUpgrade: () -> Unit
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Pro banner
                Card(
                    onClick = onProUpgrade,
                    colors = CardDefaults.cardColors(containerColor = Primary30),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Upgrade to Pro ⭐", style = MaterialTheme.typography.titleMedium, color = Secondary80)
                            Text("4K export, AI features, 500+ effects", style = MaterialTheme.typography.bodySmall, color = Primary90)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = Secondary80)
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { SettingsSectionHeader("Export") }
            item { SettingsItem(Icons.Default.HighQuality, "Default Export Quality", "1080p") }
            item { SettingsItem(Icons.Default.AspectRatio, "Default Aspect Ratio", "9:16 (Reels)") }
            item { SettingsItem(Icons.Default.Speed, "Default Frame Rate", "30fps") }

            item { SettingsSectionHeader("Storage") }
            item { SettingsItem(Icons.Default.Storage, "Cache Size", "245 MB") }
            item { SettingsItem(Icons.Default.DeleteSweep, "Clear Cache", null) }

            item { SettingsSectionHeader("Account") }
            item { SettingsItem(Icons.Default.CloudSync, "Backup Projects", "Off") }
            item { SettingsItem(Icons.Default.Download, "Export My Data", null) }
            item { SettingsItem(Icons.Default.Delete, "Delete Account", null) }

            item { SettingsSectionHeader("About") }
            item { SettingsItem(Icons.Default.Info, "Version", "1.0.0") }
            item { SettingsItem(Icons.Default.Policy, "Privacy Policy", null) }
            item { SettingsItem(Icons.Default.Description, "Open Source Licenses", null) }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Primary80,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsItem(icon: ImageVector, label: String, value: String?) {
    ListItem(
        headlineContent = { Text(label, color = OnSurface) },
        leadingContent = { Icon(icon, null, tint = OnSurfaceVariant) },
        trailingContent = value?.let {
            { Text(it, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
        },
        colors = ListItemDefaults.colors(containerColor = SurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    )
}
