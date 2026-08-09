package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeOptionsScreen(inputText: String, secretText: String, onBack: () -> Unit, onEncode: (String, String, String, String, Boolean, String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var algo by remember { mutableStateOf("AES-256") }
    var stegoMethod by remember { mutableStateOf("Zero Width Characters") }
    var compress by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.encode_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = c.onBackground) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.encode_options_title), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
            Spacer(Modifier.height(20.dp))
            // Algorithm
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, null, Modifier.size(22.dp), tint = c.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.encode_algorithm), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                    Text(algo, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            // Stego Method
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Code, null, Modifier.size(22.dp), tint = c.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.encode_stego_method), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                    Text(stegoMethod, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            // Compression toggle
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Compress, null, Modifier.size(22.dp), tint = c.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.encode_compression), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                    Switch(compress, { compress = it }, colors = SwitchDefaults.colors(checkedThumbColor = c.primary, checkedTrackColor = c.primary.copy(alpha = 0.3f)))
                }
            }
            Spacer(Modifier.height(12.dp))
            // Password
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Key, null, Modifier.size(22.dp), tint = c.onSurface)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.encode_password), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                        TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) stringResource(R.string.encode_password_hide) else stringResource(R.string.encode_password_set), color = c.primary) }
                    }
                    if (showPassword) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.encode_password_placeholder), color = c.onSurfaceVariant.copy(alpha = 0.4f)) }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surface, unfocusedContainerColor = c.surface))
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onEncode(inputText, secretText, algo, stegoMethod, compress, password) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary)) { Text(stringResource(R.string.encode_encode_btn), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall) }
            Spacer(Modifier.height(24.dp))
        }
    }
}