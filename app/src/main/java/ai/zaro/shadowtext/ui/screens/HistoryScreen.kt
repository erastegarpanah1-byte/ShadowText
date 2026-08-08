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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HistoryItem(val id: String, val type: String, val timestamp: String, val size: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val c = MaterialTheme.colorScheme
    val items = remember { listOf(HistoryItem("1","Encoded Text","May 28, 2024 - 10:30","2.1 KB"),HistoryItem("2","Decoded Text","May 24, 2024 - 10:25","1.9 KB"),HistoryItem("3","Encoded File","May 16, 2024 - 23:59","3.4 MB"),HistoryItem("4","Decoded File","May 10, 2024 - 14:35","2.7 MB")) }
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text("History",fontWeight=FontWeight.SemiBold,color=c.onBackground)},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Outlined.History,null,Modifier.size(64.dp),tint=c.onSurfaceVariant.copy(alpha=0.3f));Spacer(Modifier.height(16.dp));Text("No history yet",style=MaterialTheme.typography.bodyLarge,color=c.onSurfaceVariant);Spacer(Modifier.height(4.dp));Text("Your encoded and decoded items will appear here",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.6f))}}
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding),contentPadding=PaddingValues(horizontal=16.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(items){item->val isEncoded=item.type.contains("Encoded");val accent=if(isEncoded)c.primary else c.secondary;Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha=0.15f)),contentAlignment=Alignment.Center){Icon(if(isEncoded)Icons.Filled.Lock else Icons.Filled.Search,null,Modifier.size(22.dp),tint=accent)};Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(item.type,style=MaterialTheme.typography.titleSmall.copy(fontWeight=FontWeight.SemiBold),color=c.onSurface);Text(item.timestamp,style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant)};Text(item.size,style=MaterialTheme.typography.labelMedium,color=c.onSurfaceVariant)}}}}
        }
    }
}
