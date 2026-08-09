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
    var theme by remember { mutableStateOf(if(isDarkMode)"Dark" else "Light") }

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
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Appearance ──
            SectionTitle("Appearance")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                SettingsRow(
                    icon = Icons.Outlined.Palette,
                    title = "Theme",
                    value = theme,
                    onClick = {
                        theme = when (theme) {
                            "Dark" -> "Light"
                            "Light" -> "System"
                            else -> "Dark"
                        }
                        onToggleDarkMode(theme == "Dark")
                    },
                    showDivider = false
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Data ──
            SectionTitle("Data")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                SettingsRow(
                    icon = Icons.Outlined.Delete,
                    title = "Clear History",
                    value = "",
                    onClick = { /* TODO: clear history */ },
                    showDivider = false
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── About ──
            SectionTitle("About")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "ShadowText",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = c.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = c.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "What is ShadowText?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = c.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "ShadowText is an offline text steganography engine that lets you hide secret messages inside ordinary text using invisible Unicode characters. No one will know a hidden message is there — it looks like normal text.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Our Goal",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = c.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "To give everyone a private, offline way to communicate securely. Whether you are a journalist, activist, or just someone who values privacy — ShadowText puts control back in your hands.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "How It Works",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = c.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "ShadowText encodes your secret data into invisible Zero-Width Characters and hides them inside a cover text. The output looks identical to the original text, but carries your hidden payload. Only someone with ShadowText (and the optional password) can extract it.

All processing happens locally on your device — nothing ever leaves your phone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Security footer
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "SECURITY FIRST",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                    color = c.onSurfaceVariant.copy(alpha = 0.35f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "All processing is done on your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    "Your data never leaves your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    val c = MaterialTheme.colorScheme
    Column {
        Surface(onClick = onClick, color = c.surfaceVariant) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                if (value.isNotEmpty()) {
                    Text(value, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                }
                Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = c.outline.copy(alpha = 0.15f)
            )
        }
    }
}
