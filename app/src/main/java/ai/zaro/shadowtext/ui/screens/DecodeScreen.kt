package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.DecodeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val Gold = Color(0xFFD4A574)
private val TealAccent = Color(0xFF2ED4B4)
private val DimWhite = Color(0xFFC1C6CF)
private val NavyCard = Color(0xFF111D30)
private val NavyBorder = Color(0xFF1E3050)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeScreen(onNavigateBack: () -> Unit, onDecodeComplete: (String) -> Unit, viewModel: DecodeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.result) { state.result?.let { onDecodeComplete("done") } }

    Scaffold(containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("Decode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TealAccent) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Info, null, Modifier.size(22.dp), tint = TealAccent); Spacer(Modifier.width(10.dp)); Text("Paste any text that might contain hidden data — we'll auto-detect it.", style = MaterialTheme.typography.bodyMedium, color = DimWhite.copy(alpha = 0.7f)) }
                }
                Spacer(Modifier.height(14.dp))
                state.detection?.let { detection ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (detection.hasHiddenPayload) Color(0xFF0F3D2E).copy(alpha = 0.5f) else NavyCard), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (detection.hasHiddenPayload) TealAccent.copy(alpha = 0.35f) else NavyBorder)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (detection.hasHiddenPayload) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff, null, Modifier.size(24.dp), tint = if (detection.hasHiddenPayload) TealAccent else DimWhite.copy(alpha = 0.4f))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(if (detection.hasHiddenPayload) "Hidden data detected!" else "No hidden data detected", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = if (detection.hasHiddenPayload) TealAccent else DimWhite)
                                if (detection.hasHiddenPayload) {
                                    detection.payloadSizeBytes.takeIf { it > 0 }?.let { Text("Size: ${formatDecodeSize(it.toLong())}", style = MaterialTheme.typography.bodySmall, color = TealAccent.copy(alpha = 0.7f)) }
                                    detection.encodingScheme?.let { Text("Scheme: $it", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f)) }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }
                OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp), label = { Text("Paste text here") }, placeholder = { Text("The stego text goes here...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealAccent.copy(alpha = 0.6f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, focusedLabelColor = TealAccent, cursorColor = TealAccent))
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.decode() }, enabled = state.inputText.isNotBlank() && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color(0xFF00382E)); Spacer(Modifier.width(10.dp)); Text("Extracting...") }
                    else { Icon(Icons.Outlined.FindInPage, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Extract Hidden Data", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                }
                state.error?.let { error ->
                    Spacer(Modifier.height(14.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4), style = MaterialTheme.typography.bodyMedium) } }
                }
            }
        }
    }
}

private fun formatDecodeSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
}
