package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HistoryItem(val id: String, val type: String, val ts: String, val size: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val c = MaterialTheme.colorScheme
    val items = remember { listOf(HistoryItem("1","Encoded Text","May 28, 2024 - 10:20","2.1 KB"),HistoryItem("2","Decoded Text","May 28, 2024 - 10:25","1.8 KB"),HistoryItem("3","Encoded Text","May 28, 2024 - 22:20","3.4 MB"),HistoryItem("4","Decoded Text","May 18, 2024 - 21:15","2.7 KB")) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text("History", fontWeight = FontWeight.SemiBold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Outlined.History, null, Modifier.size(64.dp), tint = c.onSurfaceVariant.copy(alpha = 0.3f)); Spacer(Modifier.height(16.dp)); Text("No history yet", style = MaterialTheme.typography.bodyLarge, color = c.onSurfaceVariant); Spacer(Modifier.height(4.dp)); Text("Your encoded and decoded items will appear here", style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.6f)) }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(p).padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { it ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(44.dp).background(if (it.type.contains("Encoded")) c.primary.copy(alpha = 0.15f) else c.secondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(if (it.type.contains("Encoded")) Icons.Filled.Lock else Icons.Filled.Search, null, Modifier.size(22.dp), tint = if (it.type.contains("Encoded")) c.primary else c.secondary) }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) { Text(it.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onSurface); Text(it.ts, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
                            Text(it.size, style = MaterialTheme.typography.labelMedium, color = c.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
