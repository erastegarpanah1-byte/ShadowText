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
fun DecodeOptionsScreen(inputText: String, onBack: () -> Unit, onResult: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var stego by remember { mutableStateOf("Auto Detect") }
    var pwd by remember { mutableStateOf("") }
    var showPwd by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text("Decode", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onBackground)
            Spacer(Modifier.height(16.dp))
            Surface(onClick = { stego = if (stego == "Auto Detect") "Zero Width Characters" else "Auto Detect" }, modifier = Modifier.fillMaxWidth(), color = c.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Code, null, Modifier.size(22.dp), tint = c.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Text("Steganography Method", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                    Text(stego, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Key, null, Modifier.size(22.dp), tint = c.onSurface)
                Spacer(Modifier.width(12.dp))
                Text("Password", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                Text(if (pwd.isEmpty()) "Off" else "Set", style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            }
            TextButton(onClick = { showPwd = !showPwd }) { Text(if (showPwd) "Hide password" else "Password (Optional)", color = c.primary) }
            if (showPwd) { OutlinedTextField(pwd, { pwd = it }, Modifier.fillMaxWidth(), placeholder = { Text("Enter password", color = c.onSurfaceVariant.copy(alpha = 0.4f)) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant, unfocusedContainerColor = c.surfaceVariant), shape = RoundedCornerShape(12.dp)) }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { onResult("[DECODED] ${inputText.take(100)}") }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary), shape = RoundedCornerShape(14.dp)) { Text("Decode", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
