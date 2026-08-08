package ai.zaro.shadowtext.ui.screens
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeResultScreen(stegoText: String, onBack: () -> Unit, onNew: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    Scaffold(snackbarHost={SnackbarHost(snackbar)},containerColor=c.background, topBar={TopAppBar(title={Text("Encode",fontWeight=FontWeight.SemiBold,color=c.onBackground)},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=c.onBackground)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal=24.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))
            Box(Modifier.size(80.dp).clip(CircleShape).background(c.primary.copy(alpha=0.15f)),contentAlignment=Alignment.Center){Icon(Icons.Filled.CheckCircle,null,Modifier.size(48.dp),tint=c.primary)}
            Spacer(Modifier.height(24.dp))
            Text("Encoding Successful!",style=MaterialTheme.typography.headlineSmall.copy(fontWeight=FontWeight.Bold),color=c.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("Your secret data has been hidden securely.",style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant,textAlign=TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(16.dp)){Text("Hidden Text",style=MaterialTheme.typography.labelLarge.copy(fontWeight=FontWeight.Medium),color=c.onSurfaceVariant);Spacer(Modifier.height(8.dp));Text(stegoText.take(300)+if(stegoText.length>300)"..." else "",style=MaterialTheme.typography.bodySmall,color=c.onSurface)}}
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                OutlinedButton(onClick={val i=Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,stegoText)};context.startActivity(Intent.createChooser(i,"Share"))},modifier=Modifier.weight(1f).height(50.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=c.primary)){Icon(Icons.Outlined.Save,null,Modifier.size(18.dp));Spacer(Modifier.width(8.dp));Text("Save Output",fontWeight=FontWeight.SemiBold)}
                Button(onClick={clipboard.setText(AnnotatedString(stegoText));scope.launch{snackbar.showSnackbar("Copied!")}},modifier=Modifier.weight(1f).height(50.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=c.primary,contentColor=c.onPrimary)){Icon(Icons.Outlined.Share,null,Modifier.size(18.dp));Spacer(Modifier.width(8.dp));Text("Share",fontWeight=FontWeight.SemiBold)}
            }
            Spacer(Modifier.height(28.dp))
            TextButton(onClick=onNew){Icon(Icons.Filled.Add,null,Modifier.size(16.dp));Spacer(Modifier.width(6.dp));Text("New Encode",color=c.primary)}
            Spacer(Modifier.height(24.dp))
        }
    }
}
