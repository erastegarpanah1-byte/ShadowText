package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(onBack: () -> Unit, onNext: (String, String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var cover by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text("Encode", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("Cover Text", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(cover, { if (it.length <= 5000) cover = it }, Modifier.fillMaxWidth().heightIn(140.dp), placeholder = { Text("Paste or type your cover text here...", color = c.onSurfaceVariant.copy(alpha = 0.5f)) }, maxLines = 8, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant, unfocusedContainerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp))
            Text("${cover.length}/5000", style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, end = 4.dp))
            Spacer(Modifier.height(24.dp))
            Text("Secret Text", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(secret, { if (it.length <= 2000) secret = it }, Modifier.fillMaxWidth().heightIn(120.dp), placeholder = { Text("Type your secret message here...", color = c.onSurfaceVariant.copy(alpha = 0.5f)) }, maxLines = 6, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant, unfocusedContainerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp))
            Text("${secret.length}/2000", style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, end = 4.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onNext(cover, secret) }, enabled = cover.isNotBlank() && secret.isNotBlank(), modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary, disabledContainerColor = c.outline.copy(alpha = 0.3f)), shape = RoundedCornerShape(14.dp)) { Text("Next", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall); Spacer(Modifier.width(8.dp)); Icon(Icons.Filled.ArrowForward, null, Modifier.size(20.dp)) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
