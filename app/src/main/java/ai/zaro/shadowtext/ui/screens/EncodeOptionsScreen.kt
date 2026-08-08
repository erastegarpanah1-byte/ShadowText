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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeOptionsScreen(coverText: String, secretText: String, onBack: () -> Unit, onResult: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var algo by remember { mutableStateOf("AES-256") }
    var stego by remember { mutableStateOf("Zero Width Characters") }
    var compress by remember { mutableStateOf(false) }
    var pwd by remember { mutableStateOf("") }
    var showPwd by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text("Encode", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onBackground)
            Spacer(Modifier.height(16.dp))
            SettingRow(Icons.Outlined.Lock, "Algorithm", algo, { algo = if (algo == "AES-256") "AES-128" else "AES-256" })
            HorizontalDivider(color = c.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
            SettingRow(Icons.Outlined.Code, "Steganography Method", stego, { stego = if (stego == "Zero Width Characters") "Homoglyph" else "Zero Width Characters" })
            HorizontalDivider(color = c.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Compress, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text("Compression", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                Switch(compress, { compress = it }, colors = SwitchDefaults.colors(checkedThumbColor = c.primary, checkedTrackColor = c.primary.copy(alpha = 0.3f)))
            }
            HorizontalDivider(color = c.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Key, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text(if (pwd.isEmpty()) "Password (Optional)" else "Password Set", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                TextButton(onClick = { showPwd = !showPwd }) { Text(if (showPwd) "Hide" else "Set", color = c.primary) }
            }
            if (showPwd) {
                OutlinedTextField(pwd, { pwd = it }, Modifier.fillMaxWidth(), placeholder = { Text("Enter password", color = c.onSurfaceVariant.copy(alpha = 0.4f)) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant, unfocusedContainerColor = c.surfaceVariant), shape = RoundedCornerShape(12.dp))
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onResult("[ENCODED] $secretText") }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary), shape = RoundedCornerShape(14.dp)) { Text("Encode", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), color = c.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(22.dp), tint = c.onSurface)
            Spacer(Modifier.width(12.dp))
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
            Text(value, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
        }
    }
}
