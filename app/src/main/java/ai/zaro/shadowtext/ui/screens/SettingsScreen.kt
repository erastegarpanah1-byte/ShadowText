package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(isDarkMode: Boolean = true, onToggleDarkMode: (Boolean) -> Unit = {}) {
    val c = MaterialTheme.colorScheme
    var theme by remember { mutableStateOf(if (isDarkMode) "Dark" else "Light") }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History", color = c.onBackground) },
            text = { Text("Are you sure you want to delete all history entries? This action cannot be undone.", color = c.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Delete", color = c.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = c.onSurfaceVariant)
                }
            },
            containerColor = c.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = c.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold, color = c.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // ===== GENERAL =====
            SectionHeader("General")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    SettingsRow(Icons.Outlined.Palette, "Theme", theme) {
                        theme = when (theme) {
                            "Dark" -> "Light"
                            "Light" -> "System"
                            else -> "Dark"
                        }
                        onToggleDarkMode(theme == "Dark")
                    }
                    SettingsRow(Icons.Outlined.Delete, "Clear History", "", showDivider = false) {
                        showClearDialog = true
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ===== ABOUT =====
            SectionHeader("About")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "ShadowText",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = c.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = c.onSurfaceVariant
                    )
                    Spacer(Modifier.height(20.dp))

                    // What is this app?
                    Text(
                        "What is ShadowText?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = c.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "ShadowText is an offline text steganography engine for Android. It lets you hide secret messages inside ordinary-looking text using invisible Unicode characters — making your hidden data completely undetectable to the naked eye.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    // Our goal
                    Text(
                        "Our Goal",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = c.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "We believe privacy is a fundamental right. Our goal is to make secure, invisible communication accessible to everyone — no accounts, no internet, no tracking. Just you and your secrets, protected by strong encryption and hidden in plain sight.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    // How it's designed
                    Text(
                        "How It's Built",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = c.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Bullet("Architecture — MVVM + Clean Architecture for separation of concerns and testability")
                    Bullet("UI — Jetpack Compose with Material 3, supporting dark and light themes")
                    Bullet("Security — AES-256 encryption with advanced steganography techniques")
                    Bullet("Privacy — 100% offline, all processing happens on your device")
                    Bullet("Steganography — Zero Width Characters and Homoglyph methods")

                    Spacer(Modifier.height(16.dp))

                    // Footer
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "SECURITY FIRST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.5.sp
                            ),
                            color = c.primary.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "All processing is done on your device.\nYour data never leaves your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = c.onSurfaceVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Bullet(text: String) {
    val c = MaterialTheme.colorScheme
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("\u2022", color = c.primary, modifier = Modifier.padding(end = 8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold, letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    Column {
        Surface(onClick = onClick, color = c.surfaceVariant) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text(
                    title, Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium, color = c.onSurface
                )
                if (value.isNotEmpty()) {
                    Text(value, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                }
                Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
            }
        }
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = c.outline.copy(alpha = 0.15f))
        }
    }
}
