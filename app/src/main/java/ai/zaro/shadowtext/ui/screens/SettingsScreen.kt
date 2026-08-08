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
    var algo by remember { mutableStateOf("AES-256") }
    var stegoMethod by remember { mutableStateOf("Zero Width Characters") }
    var defaultPassword by remember { mutableStateOf(false) }
    var theme by remember { mutableStateOf(if(isDarkMode)"Dark" else "Light") }
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text("Settings",fontWeight=FontWeight.SemiBold,color=c.onBackground)},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal=24.dp)) {
            Spacer(Modifier.height(8.dp))
            Sec("Security")
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Column{SRow(Icons.Outlined.Lock,"Default Algorithm",algo,{algo=if(algo=="AES-256")"AES-128" else "AES-256"});SRow(Icons.Outlined.Code,"Steganography Method",stegoMethod,{stegoMethod=if(stegoMethod=="Zero Width Characters")"Homoglyph" else "Zero Width Characters"});SRow(Icons.Outlined.Key,"Default Password",if(defaultPassword)"On" else "Off",{defaultPassword=!defaultPassword},showDivider=false)}}
            Spacer(Modifier.height(28.dp))
            Sec("General")
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Column{SRow(Icons.Outlined.Palette,"Theme",theme,{theme=when(theme){"Dark"->"Light";"Light"->"System";else->"Dark"};onToggleDarkMode(theme=="Dark")});SRow(Icons.Outlined.Delete,"Clear History","",{},showDivider=false)}}
            Spacer(Modifier.height(28.dp))
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(16.dp)){Text("ShadowText",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.Bold),color=c.primary);Spacer(Modifier.height(4.dp));Text("Version 1.0.0",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant);Spacer(Modifier.height(8.dp));Text("Offline Text Steganography Engine",style=MaterialTheme.typography.bodyMedium,color=c.onSurface)}}
            Spacer(Modifier.height(40.dp))
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){Text("SECURITY FIRST",style=MaterialTheme.typography.labelSmall.copy(fontWeight=FontWeight.Bold,letterSpacing=2.sp),color=c.onSurfaceVariant.copy(alpha=0.4f));Spacer(Modifier.height(4.dp));Text("All processing is done on your device.",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.5f));Text("Your data never leaves your device.",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.5f));Spacer(Modifier.height(8.dp));Text("Strong encryption + advanced steganography
keeps your data safe.",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.4f),textAlign=TextAlign.Center)}
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Sec(title: String) { Text(title,style=MaterialTheme.typography.labelMedium.copy(fontWeight=FontWeight.Bold,letterSpacing=1.sp),color=MaterialTheme.colorScheme.onBackground,modifier=Modifier.padding(bottom=12.dp)) }

@Composable
private fun SRow(icon: ImageVector, title: String, value: String, onClick: () -> Unit, showDivider: Boolean = true) {
    val c = MaterialTheme.colorScheme
    Column{Surface(onClick=onClick,color=c.surfaceVariant){Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=14.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,Modifier.size(22.dp),tint=c.onSurface);Spacer(Modifier.width(12.dp));Text(title,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,color=c.onSurface);if(value.isNotEmpty()){Text(value,style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant);Spacer(Modifier.width(4.dp))};Icon(Icons.Filled.ChevronRight,null,Modifier.size(18.dp),tint=c.onSurfaceVariant)}};if(showDivider){HorizontalDivider(modifier=Modifier.padding(horizontal=16.dp),color=c.outline.copy(alpha=0.15f))}}
}
