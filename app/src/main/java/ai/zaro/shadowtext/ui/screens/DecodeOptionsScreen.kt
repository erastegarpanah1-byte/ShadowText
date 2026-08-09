package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeOptionsScreen(inputText: String, onBack: () -> Unit, onDecode: (String, String, String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var stegoMethod by remember { mutableStateOf("Auto Detect") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.decode_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = c.onBackground) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        ConstrainedColumn(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.decode_options_title), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
            Spacer(Modifier.height(20.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Code, null, Modifier.size(22.dp), tint = c.onSurface)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(stringResource(R.string.decode_stego_method), style = MaterialTheme.typography.bodyMedium, color = c.onSurface); Text(stegoMethod, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
                    Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Key, null, Modifier.size(22.dp), tint = c.onSurface)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.decode_password), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
                        TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) stringResource(R.string.encode_password_hide) else stringResource(R.string.decode_password_enter), color = c.secondary) }
                    }
                    if (showPassword) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.decode_password_placeholder), color = c.onSurfaceVariant.copy(alpha = 0.4f)) }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.secondary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surface, unfocusedContainerColor = c.surface))
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onDecode(inputText, stegoMethod, password) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.secondary, contentColor = c.onSecondary)) { Text(stringResource(R.string.decode_decode_btn), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
