package com.pocketstudios.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pocketstudios.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProUpgradeScreen(onBack: () -> Unit) {
    var selectedPlan by remember { mutableStateOf("annual") }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, "Close", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(listOf(Primary30, Surface))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Pocket Studios Pro", style = MaterialTheme.typography.headlineLarge, color = Secondary80)
                    Text("Unlock your full creative potential", style = MaterialTheme.typography.bodyMedium, color = Primary90)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Features list
            val features = listOf(
                Icons.Default.HighQuality to "4K Ultra HD Export",
                Icons.Default.AutoAwesome to "AI Auto-Captions (unlimited)",
                Icons.Default.Brush to "500+ Premium Effects & LUTs",
                Icons.Default.CloudUpload to "Cloud Project Backup",
                Icons.Default.Remove to "No Ads, Ever"
            )
            features.forEach { (icon, text) ->
                ProFeatureRow(icon = icon, text = text)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Plan selection
            PlanCard(
                title = "Annual",
                price = "$29.99",
                period = "per year",
                badge = "BEST VALUE",
                isSelected = selectedPlan == "annual",
                onClick = { selectedPlan = "annual" }
            )
            Spacer(Modifier.height(12.dp))
            PlanCard(
                title = "Monthly",
                price = "$4.99",
                period = "per month",
                badge = null,
                isSelected = selectedPlan == "monthly",
                onClick = { selectedPlan = "monthly" }
            )
            Spacer(Modifier.height(12.dp))
            PlanCard(
                title = "Lifetime",
                price = "$49.99",
                period = "one-time",
                badge = "POPULAR",
                isSelected = selectedPlan == "lifetime",
                onClick = { selectedPlan = "lifetime" }
            )

            Spacer(Modifier.height(24.dp))

            // CTA
            Button(
                onClick = { /* TODO: RevenueCat purchase flow */ },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary80),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Start Pro — ${if (selectedPlan == "annual") "$29.99/yr" else if (selectedPlan == "monthly") "$4.99/mo" else "$49.99"}", color = Surface, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { /* Restore purchases */ }) {
                Text("Restore Purchases", color = OnSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text("Cancel anytime. Subscriptions auto-renew.", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProFeatureRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Secondary80, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = OnSurface)
    }
}

@Composable
private fun PlanCard(
    title: String, price: String, period: String,
    badge: String?, isSelected: Boolean, onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary30 else SurfaceVariant
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    if (badge != null) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Secondary80, shape = RoundedCornerShape(4.dp)) {
                            Text(badge, style = MaterialTheme.typography.labelSmall, color = Surface, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(period, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
            Text(price, style = MaterialTheme.typography.headlineMedium, color = if (isSelected) Primary80 else OnSurface)
        }
    }
}
