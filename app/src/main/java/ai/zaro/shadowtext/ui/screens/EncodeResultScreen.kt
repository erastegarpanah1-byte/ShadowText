package ai.zaro.shadowtext.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val ctx = LocalContext.current
    val clip = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snack = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(snack) }, containerColor = c.background, topBar = { TopAppBar(title = { Text("Encode", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Box(Modifier.size(80.dp).background(c.primary.copy(alpha = 0.15f), RoundedCornerShape(40.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(48.dp), tint = c.primary) }
            Spacer(Modifier.height(24.dp))
            Text("Encoding Successful!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = c.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("Your secret data has been hidden securely.", style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Stego Text", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = c.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(stegoText.take(300) + if (stegoText.length > 300) "..." else "", style = MaterialTheme.typography.bodySmall, color = c.onSurface)
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { val i = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, stegoText) }; ctx.startActivity(Intent.createChooser(i, "Share")) }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(14.dp), border = ButtonDefaults.outlinedButtonBorder, colors = ButtonDefaults.outlinedButtonColors(contentColor = c.primary)) { Icon(Icons.Outlined.Save, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Save Output", fontWeight = FontWeight.SemiBold) }
                Button(onClick = { clip.setText(AnnotatedString(stegoText)); scope.launch { snack.showSnackbar("Copied!") } }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary)) { Icon(Icons.Outlined.Share, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Share", fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onNew) { Icon(Icons.Filled.Add, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("New Encode", color = c.primary) }
        }
    }
}
