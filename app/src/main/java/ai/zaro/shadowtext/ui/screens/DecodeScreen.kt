package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.ui.viewmodel.DecodeInputMode
import ai.zaro.shadowtext.ui.viewmodel.DecodeUiState
import ai.zaro.shadowtext.ui.viewmodel.DecodeViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { viewModel.loadFileForDecode(it) } }
    LaunchedEffect(state.result) { state.result?.let { onDecodeComplete("done") } }
    Scaffold(containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("Decode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.Filled.ArrowBack, "Back", tint = TealAccent) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("How do you want to provide the stego text?", style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { ModeCard(Icons.Outlined.EditNote, "Paste Text", "Paste stego text directly", state.inputMode == DecodeInputMode.TEXT, TealAccent, { viewModel.setInputMode(DecodeInputMode.TEXT) }, Modifier.weight(1f)); ModeCard(Icons.Outlined.UploadFile, "Open File", "Load a text file", state.inputMode == DecodeInputMode.FILE, Gold, { viewModel.setInputMode(DecodeInputMode.FILE) }, Modifier.weight(1f)) }
                Spacer(Modifier.height(14.dp)); state.detection?.let { d -> DetectionCard(d) }; Spacer(Modifier.height(14.dp))
                when (state.inputMode) { DecodeInputMode.TEXT -> PasteInput(state, viewModel); DecodeInputMode.FILE -> FileDecodeInput(state, viewModel, filePicker) }
                Spacer(Modifier.height(14.dp))
                Button(onClick = { viewModel.decode() }, enabled = state.inputText.isNotBlank() && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E), disabledContainerColor = TealAccent.copy(alpha = 0.3f), disabledContentColor = TealAccent.copy(alpha = 0.5f)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), color = Color(0xFF00382E), trackColor = TealAccent.copy(alpha = 0.3f)) } else { Icon(Icons.Outlined.FindInPage, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Extract Hidden Data", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                }
                state.error?.let { e -> Spacer(Modifier.height(14.dp)); ErrorCard(e) }; Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable private fun ErrorCard(error: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4), style = MaterialTheme.typography.bodyMedium) } } }

@Composable private fun DetectionCard(d: DetectionResult) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (d.hasHiddenPayload) Color(0xFF0F3D2E).copy(alpha = 0.5f) else NavyCard), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (d.hasHiddenPayload) TealAccent.copy(alpha = 0.35f) else NavyBorder)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(if (d.hasHiddenPayload) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff, null, Modifier.size(24.dp), tint = if (d.hasHiddenPayload) TealAccent else DimWhite.copy(alpha = 0.4f)); Spacer(Modifier.width(10.dp)); Column { Text(if (d.hasHiddenPayload) "Hidden data detected!" else "No hidden data detected", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = if (d.hasHiddenPayload) TealAccent else DimWhite); if (d.hasHiddenPayload) { d.payloadSizeBytes.takeIf { it > 0 }?.let { Text("Size: ${formatDecodeSize(it.toLong())}", style = MaterialTheme.typography.bodySmall, color = TealAccent.copy(alpha = 0.7f)) }; d.encodingScheme?.let { Text("Scheme: $it", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f)) } } } } } }

@Composable private fun ModeCard(icon: ImageVector, title: String, subtitle: String, selected: Boolean, accent: Color, onClick: () -> Unit, modifier: Modifier) { Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (selected) accent.copy(alpha = 0.12f) else NavyCard), border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selected) accent.copy(alpha = 0.5f) else NavyBorder)) { Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, Modifier.size(32.dp), tint = if (selected) accent else DimWhite.copy(alpha = 0.5f)); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = if (selected) accent else DimWhite, textAlign = TextAlign.Center); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.labelSmall, color = DimWhite.copy(alpha = 0.45f), textAlign = TextAlign.Center) } } }

@Composable private fun PasteInput(state: DecodeUiState, viewModel: DecodeViewModel) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.25f))) { Column(Modifier.padding(14.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.EditNote, null, Modifier.size(20.dp), tint = TealAccent); Spacer(Modifier.width(8.dp)); Text("Paste stego text", style = MaterialTheme.typography.labelMedium, color = TealAccent) }; Spacer(Modifier.height(8.dp)); OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp), placeholder = { Text("Paste the stego text here...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 10, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealAccent.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = TealAccent)) } } }

@Composable private fun FileDecodeInput(state: DecodeUiState, viewModel: DecodeViewModel, filePicker: androidx.activity.result.ActivityResultLauncher<Array<String>>) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.25f))) { Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Outlined.Description, null, Modifier.size(40.dp), tint = Gold); Spacer(Modifier.height(10.dp)); Text("Select a text file containing stego text", style = MaterialTheme.typography.titleSmall, color = Gold); Spacer(Modifier.height(4.dp)); Text("Supports .txt, .text, .sms, or any plain-text file", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.45f)); Spacer(Modifier.height(16.dp)); if (state.selectedFileName != null) { Surface(color = Color(0xFF2A1F10), shape = RoundedCornerShape(10.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = Gold); Spacer(Modifier.width(10.dp)); Text(state.selectedFileName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = DimWhite) } }; if (state.inputText.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(state.inputText.take(200) + if (state.inputText.length > 200) "..." else "", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 8.dp)) }; Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = { filePicker.launch(arrayOf("text/*", "application/octet-stream")) }, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.5f))) { Icon(Icons.Filled.SwapHoriz, null, tint = Gold); Spacer(Modifier.width(8.dp)); Text("Change File", color = Gold) } } else { Button(onClick = { filePicker.launch(arrayOf("text/*", "application/octet-stream")) }, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF0B1E33)), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Filled.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose Text File", fontWeight = FontWeight.SemiBold) } } } } }

private fun formatDecodeSize(bytes: Long): String = when { bytes < 1024 -> "$bytes B"; bytes < 1024 * 1024 -> "${bytes / 1024} KB"; else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB" }
