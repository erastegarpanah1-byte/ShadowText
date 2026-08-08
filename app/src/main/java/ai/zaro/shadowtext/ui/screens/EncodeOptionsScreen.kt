package ai.zaro.shadowtext.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeOptionsScreen(inputText: String, secretText: String, onBack: () -> Unit, onEncode: (String,String,String,String,Boolean,String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var algo by remember { mutableStateOf("AES-256") }
    var stegoMethod by remember { mutableStateOf("Zero Width Characters") }
    var compress by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text("Encode",fontWeight=FontWeight.SemiBold,color=c.onBackground)},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=c.onBackground)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=24.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("Options",style=MaterialTheme.typography.titleMedium.copy(fontWeight=FontWeight.SemiBold),color=c.onBackground)
            Spacer(Modifier.height(20.dp))
            SettRow(Icons.Outlined.Lock,"Algorithm",algo,listOf("AES-256","AES-128")){algo=it}
            SettRow(Icons.Outlined.Code,"Steganography Method",stegoMethod,listOf("Zero Width Characters","Homoglyph")){stegoMethod=it}
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Outlined.Compress,null,Modifier.size(22.dp),tint=c.onSurface);Spacer(Modifier.width(12.dp));Text("Compression",Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,color=c.onSurface);Switch(compress,{compress=it},colors=SwitchDefaults.colors(checkedThumbColor=c.primary,checkedTrackColor=c.primary.copy(alpha=0.3f)))}}
            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(16.dp)){Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Outlined.Key,null,Modifier.size(22.dp),tint=c.onSurface);Spacer(Modifier.width(12.dp));Text("Password (Optional)",Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,color=c.onSurface);TextButton(onClick={showPassword=!showPassword}){Text(if(showPassword)"Hide" else "Set",color=c.primary)}};if(showPassword){Spacer(Modifier.height(8.dp));OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),placeholder={Text("Enter password",color=c.onSurfaceVariant.copy(alpha=0.4f))},singleLine=true,shape=RoundedCornerShape(12.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=c.primary,unfocusedBorderColor=c.outline,focusedContainerColor=c.surface,unfocusedContainerColor=c.surface))}}}
            Spacer(Modifier.height(32.dp))
            Button(onClick={onEncode(inputText,secretText,algo,stegoMethod,compress,password)},modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=c.primary,contentColor=c.onPrimary)){Text("Encode",fontWeight=FontWeight.SemiBold,style=MaterialTheme.typography.titleSmall)}
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettRow(icon: ImageVector, title: String, value: String, choices: List<String>, onSelect: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,Modifier.size(22.dp),tint=c.onSurface);Spacer(Modifier.width(12.dp));Text(title,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,color=c.onSurface)
        Box{TextButton(onClick={expanded=true}){Text(value,style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant);Icon(Icons.Filled.ChevronRight,null,Modifier.size(18.dp),tint=c.onSurfaceVariant)}
        DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}){choices.forEach{ch->DropdownMenuItem(text={Text(ch)},onClick={onSelect(ch);expanded=false})}}}}
    }
    Spacer(Modifier.height(12.dp))
}
