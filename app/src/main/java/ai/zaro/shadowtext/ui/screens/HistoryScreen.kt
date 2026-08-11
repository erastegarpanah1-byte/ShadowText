package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val c = MaterialTheme.colorScheme; val context = LocalContext.current
    var entries by remember { mutableStateOf(HistoryStore.getAll(context)) }
    var filter by remember { mutableStateOf("all") }
    var showClearDialog by remember { mutableStateOf(false) }
    var detailEntry by remember { mutableStateOf<HistoryEntry?>(null) }
    fun refresh() { entries = HistoryStore.getAll(context) }

    if (showClearDialog) {
        AlertDialog(onDismissRequest = { showClearDialog = false }, title = { Text(stringResource(R.string.history_clear_title)) }, text = { Text(stringResource(R.string.history_clear_message)) }, confirmButton = { TextButton(onClick = { HistoryStore.clear(context); refresh(); showClearDialog = false }) { Text(stringResource(R.string.history_clear_confirm), color = c.error) } }, dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.history_clear_cancel)) } }, containerColor = c.surface, shape = RoundedCornerShape(20.dp))
    }

    if (detailEntry != null) { HistoryDetailScreen(entry = detailEntry!!, onDelete = { HistoryStore.remove(context, detailEntry!!.id); detailEntry = null; refresh() }, onBack = { detailEntry = null }); return }

    val filtered = if (filter == "all") entries else entries.filter { it.type == filter }
    val grouped = groupByDate(filtered)

    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.history_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background), actions = { if (entries.isNotEmpty()) { IconButton(onClick = { showClearDialog = true }) { Icon(Icons.Outlined.Delete, stringResource(R.string.history_clear_all), tint = c.onSurfaceVariant) } } }) }) { padding ->
        Column(Modifier.padding(padding)) {
            if (entries.any { it.type == "encode" } && entries.any { it.type == "decode" }) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = filter == "all", onClick = { filter = "all" }, label = { Text(stringResource(R.string.history_filter_all)) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary.copy(alpha = 0.15f), selectedLabelColor = c.primary))
                    FilterChip(selected = filter == "encode", onClick = { filter = "encode" }, label = { Text(stringResource(R.string.history_filter_encode)) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary.copy(alpha = 0.15f), selectedLabelColor = c.primary))
                    FilterChip(selected = filter == "decode", onClick = { filter = "decode" }, label = { Text(stringResource(R.string.history_filter_decode)) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.secondary.copy(alpha = 0.15f), selectedLabelColor = c.secondary))
                }
            }
            if (grouped.isEmpty()) {
                ConstrainedColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.History, null, Modifier.size(64.dp), tint = c.onSurfaceVariant.copy(alpha = 0.3f))
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = c.onSurfaceVariant, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.history_empty_desc), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    grouped.forEach { (dateLabel, dayEntries) ->
                        item(key = dateLabel) { Text(dateLabel, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = c.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) }
                        items(dayEntries.reversed(), key = { "entry_${it.id}" }) { entry -> HistoryRow(entry = entry, onClick = { detailEntry = entry }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme; val isEncode = entry.type == "encode"; val accent = if (isEncode) c.primary else c.secondary
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp), color = c.surfaceVariant.copy(alpha = 0.6f)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(10.dp), color = accent.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) { Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(if (isEncode) "E" else "D", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = accent) } }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(if (isEncode) R.string.home_encode else R.string.home_decode), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = c.onSurface)
                Text(stringResource(R.string.history_mode_text_in_text), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.7f))
                Spacer(Modifier.height(2.dp))
                Text("${entry.inputPreview.length} \u2192 ${entry.outputPreview.length} chars", style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant.copy(alpha = 0.5f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatTime(entry.timestamp), style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant.copy(alpha = 0.5f))
                Spacer(Modifier.height(2.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = if (entry.status == "success") c.primary.copy(alpha = 0.12f) else c.error.copy(alpha = 0.12f)) { Text(if (entry.status == "success") "\u2713" else "\u2717", modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp), style = MaterialTheme.typography.labelSmall, color = if (entry.status == "success") c.primary else c.error) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailScreen(entry: HistoryEntry, onDelete: () -> Unit, onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme; val isEncode = entry.type == "encode"; val accent = if (isEncode) c.primary else c.secondary
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }; val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.history_detail_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, navigationIcon = { TextButton(onClick = onBack) { Text(stringResource(R.string.back), color = c.primary) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        ConstrainedColumn(modifier = Modifier.padding(padding)) {
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(48.dp), color = accent.copy(alpha = 0.1f), modifier = Modifier.size(64.dp)) { Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(if (isEncode) "E" else "D", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = accent) } }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(if (isEncode) R.string.home_encode else R.string.home_decode), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = c.onBackground)
            Spacer(Modifier.height(28.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    DetailRow(stringResource(R.string.history_detail_type), stringResource(if (isEncode) R.string.home_encode else R.string.home_decode), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f))
                    DetailRow(stringResource(R.string.history_detail_mode), stringResource(R.string.history_mode_text_in_text), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f))
                    DetailRow(stringResource(R.string.history_detail_input), stringResource(R.string.history_chars, entry.inputPreview.length), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f))
                    DetailRow(stringResource(R.string.history_detail_output), stringResource(R.string.history_chars, entry.outputPreview.length), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f))
                    DetailRow(stringResource(R.string.history_detail_date), dateFormat.format(Date(entry.timestamp)), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f))
                    DetailRow(stringResource(R.string.history_detail_time), timeFormat.format(Date(entry.timestamp)), c.onSurfaceVariant.copy(alpha = 0.7f), c.onSurface, c.outline.copy(alpha = 0.08f), false)
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = if (entry.status == "success") c.primary.copy(alpha = 0.1f) else c.error.copy(alpha = 0.1f)) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (entry.status == "success") "\u2713" else "\u2717", color = if (entry.status == "success") c.primary else c.error, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(if (entry.status == "success") R.string.history_status_success else R.string.history_status_failed), style = MaterialTheme.typography.bodyMedium, color = if (entry.status == "success") c.primary else c.error)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Delete, null, Modifier.size(18.dp), tint = c.error); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.history_delete_item), color = c.error) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, labelColor: Color, valueColor: Color, dividerColor: Color, showDivider: Boolean = true) {
    Column {
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, style = MaterialTheme.typography.bodyMedium, color = labelColor); Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = valueColor) }
        if (showDivider) HorizontalDivider(color = dividerColor)
    }
}

private fun groupByDate(entries: List<HistoryEntry>): List<Pair<String, List<HistoryEntry>>> {
    val today = Calendar.getInstance(); val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault()); val dayMap = LinkedHashMap<String, MutableList<HistoryEntry>>()
    for (entry in entries.sortedByDescending { it.timestamp }) {
        val cal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
        val label = when { isSameDay(cal, today) -> "Today"; isSameDay(cal, yesterday) -> "Yesterday"; else -> dateFormat.format(Date(entry.timestamp)) }
        dayMap.getOrPut(label) { mutableListOf() }.add(entry)
    }
    return dayMap.toList()
}

private fun isSameDay(a: Calendar, b: Calendar) = a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
