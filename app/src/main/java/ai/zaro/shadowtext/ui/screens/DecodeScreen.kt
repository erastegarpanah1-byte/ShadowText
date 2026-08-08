package ai.zaro.shadowtext.ui.screens

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
fun DecodeScreen(onBack: () -> Unit, onNext: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var text by remember { mutableStateOf("") }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text("Decode", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.onBackground)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("Input", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(text, { if (it.length <= 5000) text = it }, Modifier.fillMaxWidth().heightIn(220.dp), placeholder = { Text("Paste or type your encoded text here...", color = c.onSurfaceVariant.copy(alpha = 0.5f)) }, maxLines = 12, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.primary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant, unfocusedContainerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp))
            Text("${text.length}/5000", style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, end = 4.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onNext(text) }, enabled = text.isNotBlank(), modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary, disabledContainerColor = c.outline.copy(alpha = 0.3f)), shape = RoundedCornerShape(14.dp)) { Text("Next", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall); Spacer(Modifier.width(8.dp)); Icon(Icons.Filled.ArrowForward, null, Modifier.size(20.dp)) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
