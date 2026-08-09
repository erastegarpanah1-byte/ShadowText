package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseInputScreen(mode: String, onBack: () -> Unit, onTextSelected: () -> Unit, onFileSelected: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val accent = if (mode == "encode") c.primary else c.secondary
    val title = if (mode == "encode") "Encode" else "Decode"
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text(title,fontWeight=FontWeight.SemiBold,color=c.onBackground)},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=c.onBackground)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
            Spacer(Modifier.height(24.dp))
            Text("Choose Input Type",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.SemiBold),color=c.onBackground,textAlign=TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("What do you want to use?",style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant,textAlign=TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            // Text – horizontal card
            Card(onClick=onTextSelected,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),elevation=CardDefaults.cardElevation(defaultElevation=2.dp)) {
                Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha=0.1f)),contentAlignment=Alignment.Center){Icon(Icons.Outlined.TextFields,null,Modifier.size(28.dp),tint=accent)}
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) { Text("Text",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.Bold),color=c.onSurface); Text("Use text as container",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant) }
                }
            }

            Spacer(Modifier.height(16.dp))

            // File – disabled horizontal card
            Card(enabled=false,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant.copy(alpha=0.5f),disabledContainerColor=c.surfaceVariant.copy(alpha=0.5f)),elevation=CardDefaults.cardElevation(defaultElevation=0.dp)) {
                Box {
                    Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically) {
                        Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha=0.05f)),contentAlignment=Alignment.Center){Icon(Icons.Outlined.InsertDriveFile,null,Modifier.size(28.dp),tint=c.onSurfaceVariant.copy(alpha=0.3f))}
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) { Text("File",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.Bold),color=c.onSurfaceVariant.copy(alpha=0.4f)); Text("Available in upcoming updates",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.35f)) }
                    }
                    // Coming Soon badge
                    Box(Modifier.align(Alignment.TopEnd).padding(12.dp).background(accent.copy(alpha=0.15f),RoundedCornerShape(8.dp)).padding(horizontal=10.dp,vertical=4.dp)) {
                        Text("Coming Soon",style=MaterialTheme.typography.labelSmall.copy(fontWeight=FontWeight.Medium),color=accent)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
