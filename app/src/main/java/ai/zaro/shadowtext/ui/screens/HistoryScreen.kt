package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val c = MaterialTheme.colorScheme
    val context = LocalContext.current
    var entries by remember { mutableStateOf(HistoryStore.getAll(context)) }
    var showClearDialog by remember { mutableStateOf(false) }

    fun refresh() { entries = HistoryStore.getAll(context) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.settings_clear_title)) },
            text = { Text(stringResource(R.string.settings_clear_message)) },
            confirmButton = {
                TextButton(onClick = {
                    HistoryStore.clear(context)
                    refresh()
                    showClearDialog = false
                }) { Text(stringResource(R.string.settings_clear_confirm), color = c.error) }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.settings_clear_cancel)) } },
            containerColor = c.surface, shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = c.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background),
                actions = {
                    if (entries.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Outlined.Delete, stringResource(R.string.settings_clear_history), tint = c.onSurfaceVariant)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (entries.isEmpty()) {
            ConstrainedColumn(modifier = Modifier.padding(padding), verticalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.History, null, Modifier.size(64.dp), tint = c.onSurfaceVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = c.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.history_empty_desc), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    HistoryItem(entry)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntry) {
    val c = MaterialTheme.colorScheme
    val isEncode = entry.type == "encode"
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = c.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isEncode) c.primary.copy(alpha = 0.15f) else c.secondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        if (isEncode) stringResource(R.string.home_encode) else stringResource(R.string.home_decode),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isEncode) c.primary else c.secondary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = c.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(entry.inputPreview, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(entry.outputPreview, style = MaterialTheme.typography.bodySmall, color = c.primary.copy(alpha = 0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
