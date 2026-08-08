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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseInputScreen(mode: String, onBack: () -> Unit, onTextSelected: () -> Unit, onFileSelected: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val accent = if (mode == "encode") c.primary else c.secondary
    val title = if (mode == "encode") "Encode" else "Decode"
    Scaffold(containerColor=c.background, topBar={ TopAppBar(title={Text(title,fontWeight=FontWeight.SemiBold,color=c.onBackground)}, navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=c.onBackground)}}, colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=24.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("Choose Input Type",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.SemiBold),color=c.onBackground)
            Text("What do you want to use?",style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            SelCard(Icons.Outlined.TextFields,"Text","Use text as container",accent,onTextSelected,Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            SelCard(Icons.Outlined.InsertDriveFile,"File","Use file as container",accent,onFileSelected,Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SelCard(icon: ImageVector, title: String, subtitle: String, accent: Color, onClick: () -> Unit, modifier: Modifier) {
    val c = MaterialTheme.colorScheme
    Card(onClick=onClick,modifier=modifier,shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant)) {
        Column(Modifier.padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(accent.copy(alpha=0.1f)),contentAlignment=Alignment.Center){Icon(icon,null,Modifier.size(30.dp),tint=accent)}
            Spacer(Modifier.height(12.dp))
            Text(title,style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.Bold),color=c.onSurface,textAlign=TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(subtitle,style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant,textAlign=TextAlign.Center)
        }
    }
}
