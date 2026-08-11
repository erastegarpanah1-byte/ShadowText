package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean = true,
    onToggleDarkMode: (Boolean) -> Unit = {},
    languageCode: String = "en",
    onChangeLanguage: (String) -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val c = MaterialTheme.colorScheme
    val context = LocalContext.current
    var theme by remember { mutableStateOf(if (isDarkMode) "Dark" else "Light") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    var historyEnabled by remember { mutableStateOf(context.getSharedPreferences("shadowtext_prefs", android.content.Context.MODE_PRIVATE).getBoolean("history_enabled", true)) }
    val langLabel = if (languageCode == "fa") "فارسی" else "English"

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.settings_clear_title)) },
            text = { Text(stringResource(R.string.settings_clear_message)) },
            confirmButton = { TextButton(onClick = { HistoryStore.clear(context); showClearDialog = false }) { Text(stringResource(R.string.settings_clear_confirm), color = c.error) } },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.settings_clear_cancel)) } },
            containerColor = c.surface, shape = RoundedCornerShape(20.dp)
        )
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    TextButton(onClick = { onChangeLanguage("en"); showLangDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_language_en), fontWeight = if (languageCode == "en") FontWeight.Bold else FontWeight.Normal, color = if (languageCode == "en") c.primary else c.onSurface)
                    }
                    TextButton(onClick = { onChangeLanguage("fa"); showLangDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_language_fa), fontWeight = if (languageCode == "fa") FontWeight.Bold else FontWeight.Normal, color = if (languageCode == "fa") c.primary else c.onSurface)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface
        )
    }

    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        ConstrainedColumn(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(12.dp))

            SectionHeader(stringResource(R.string.settings_appearance))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column {
                    SettingsRow(Icons.Outlined.Palette, stringResource(R.string.settings_theme), theme) {
                        theme = when (theme) { "Dark" -> "Light"; "Light" -> "System"; else -> "Dark" }
                        onToggleDarkMode(theme == "Dark")
                    }
                    SettingsRow(Icons.Outlined.Language, stringResource(R.string.settings_language), langLabel, showDivider = false) { showLangDialog = true }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionHeader(stringResource(R.string.settings_data))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.History, null, Modifier.size(22.dp), tint = c.onSurface)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.settings_history_toggle), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                            Text(stringResource(R.string.settings_history_toggle_desc), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                        Spacer(Modifier.width(8.dp))
                        Switch(checked = historyEnabled, onCheckedChange = { historyEnabled = it; context.getSharedPreferences("shadowtext_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("history_enabled", it).apply() }, colors = SwitchDefaults.colors(checkedThumbColor = c.primary, checkedTrackColor = c.primary.copy(alpha = 0.3f)))
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = c.outline.copy(alpha = 0.15f))
                    SettingsRow(Icons.Outlined.Delete, stringResource(R.string.settings_clear_history), "", showDivider = false) { showClearDialog = true }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionHeader(stringResource(R.string.settings_about))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                SettingsRow(Icons.Outlined.Info, stringResource(R.string.settings_about), "", showDivider = false) { onNavigateToAbout() }
            }

            Spacer(Modifier.height(40.dp))
        }
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
