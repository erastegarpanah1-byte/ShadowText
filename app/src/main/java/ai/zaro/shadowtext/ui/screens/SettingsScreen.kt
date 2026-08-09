package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean = true,
    onToggleDarkMode: (Boolean) -> Unit = {},
    languageCode: String = "en",
    onChangeLanguage: (String) -> Unit = {}
) {
    val c = MaterialTheme.colorScheme
    var theme by remember { mutableStateOf(if (isDarkMode) "Dark" else "Light") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    val langLabel = if (languageCode == "fa") "\u0641\u0627\u0631\u0633\u06CC" else "English"

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.settings_clear_title)) },
            text = { Text(stringResource(R.string.settings_clear_message)) },
            confirmButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.settings_clear_confirm), color = c.error) } },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.settings_clear_cancel)) } },
            containerColor = c.surface, shape = RoundedCornerShape(20.dp)
        )
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            containerColor = c.surface, shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                TextButton(onClick = { onChangeLanguage("en"); showLangDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.settings_language_en), fontWeight = if (languageCode == "en") FontWeight.Bold else FontWeight.Normal, color = if (languageCode == "en") c.primary else c.onSurface)
                }
                TextButton(onClick = { onChangeLanguage("fa"); showLangDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.settings_language_fa), fontWeight = if (languageCode == "fa") FontWeight.Bold else FontWeight.Normal, color = if (languageCode == "fa") c.primary else c.onSurface)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(12.dp))

            SectionHeader(stringResource(R.string.settings_appearance))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column {
                    SettingsRow(Icons.Outlined.Palette, stringResource(R.string.settings_theme), theme) {
                        theme = when (theme) {
                            "Dark" -> "Light"
                            "Light" -> "System"
                            else -> "Dark"
                        }
                        onToggleDarkMode(theme == "Dark")
                    }
                    SettingsRow(Icons.Outlined.Language, stringResource(R.string.settings_language), langLabel, showDivider = false) { showLangDialog = true }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionHeader(stringResource(R.string.settings_data))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                SettingsRow(Icons.Outlined.Delete, stringResource(R.string.settings_clear_history), "", showDivider = false) { showClearDialog = true }
            }

            Spacer(Modifier.height(32.dp))

            SectionHeader(stringResource(R.string.settings_about))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("ShadowText", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.settings_version), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(20.dp))
                    Text(stringResource(R.string.settings_about_what_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(R.string.settings_about_what_body), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.settings_about_goal_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(R.string.settings_about_goal_body), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.settings_about_how_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
                    Spacer(Modifier.height(6.dp))
                    Bullet(stringResource(R.string.settings_about_how_arch))
                    Bullet(stringResource(R.string.settings_about_how_ui))
                    Bullet(stringResource(R.string.settings_about_how_security))
                    Bullet(stringResource(R.string.settings_about_how_privacy))
                    Bullet(stringResource(R.string.settings_about_how_stego))
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Text(stringResource(R.string.settings_security_first), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp), color = c.primary.copy(alpha = 0.5f), textAlign = TextAlign.Center) }
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.settings_processing) + "\n" + stringResource(R.string.settings_no_leak), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.5f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
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
    Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, value: String, showDivider: Boolean = true, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column {
        Surface(onClick = onClick, color = c.surfaceVariant) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                if (value.isNotEmpty()) { Text(value, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant); Spacer(Modifier.width(4.dp)) }
                Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
            }
        }
        if (showDivider) { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = c.outline.copy(alpha = 0.15f)) }
    }
}
