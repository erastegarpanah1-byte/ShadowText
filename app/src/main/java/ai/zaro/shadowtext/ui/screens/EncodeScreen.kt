package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.EncodeMode
import ai.zaro.shadowtext.ui.viewmodel.EncodeUiState
import ai.zaro.shadowtext.ui.viewmodel.EncodeViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val Gold = Color(0xFFD4A574)
private val TealAccent = Color(0xFF2ED4B4)
private val DimWhite = Color(0xFFC1C6CF)
private val NavyCard = Color(0xFF111D30)
private val NavyBorder = Color(0xFF1E3050)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(onNavigateBack: () -> Unit, onEncodeComplete: (String) -> Unit, viewModel: EncodeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { viewModel.loadFile(it) } }
    LaunchedEffect(state.result) { state.result?.let { onEncodeComplete(it.stegoText) } }

    Scaffold(containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("Encode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.Filled.ArrowBack, "Back", tint = Gold) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("What do you want to hide?", style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { ModeCard(Icons.Outlined.TextFields, "Hidden Message", "Type a secret text", state.mode == EncodeMode.TEXT, Gold, { viewModel.setMode(EncodeMode.TEXT) }, Modifier.weight(1f)); ModeCard(Icons.Outlined.InsertDriveFile, "Hidden File", "Images, video, PDF, etc.", state.mode == EncodeMode.FILE, TealAccent, { viewModel.setMode(EncodeMode.FILE) }, Modifier.weight(1f)) }
                Spacer(Modifier.height(20.dp))
                when (state.mode) { EncodeMode.TEXT -> TextModeContent(state, viewModel); EncodeMode.FILE -> FileModeContent(state, viewModel, filePicker) }
                Spacer(Modifier.height(16.dp))
                CarrierTextToggle(state.useCarrierText, state.carrierText, viewModel::toggleCarrierText, viewModel::setCarrierText)
                Spacer(Modifier.height(16.dp))
                val canEncode = when (state.mode) { EncodeMode.TEXT -> state.inputText.isNotBlank(); EncodeMode.FILE -> state.bytesLoaded }
                Button(onClick = { viewModel.encode() }, enabled = canEncode && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), color = Color(0xFF00382E), trackColor = TealAccent.copy(alpha = 0.3f)) } else { Icon(Icons.Filled.Lock, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Encode Now", fontWeight = FontWeight.Bold) }
                }
                state.error?.let { Spacer(Modifier.height(14.dp)); ErrorCard(it) }; Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable private fun ErrorCard(e: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(e, color = Color(0xFFFFDAD4)) } } }

@Composable private fun CarrierTextToggle(enabled: Boolean, text: String, onToggle: () -> Unit, onTextChange: (String) -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
        Column {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.Article, null, Modifier.size(22.dp), tint = if (enabled) Gold else DimWhite.copy(alpha = 0.4f)); Spacer(Modifier.width(10.dp)); Column { Text("Carrier Text", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = if (enabled) DimWhite else DimWhite.copy(alpha = 0.5f)); Text(if (enabled) "Your payload is hidden in this text" else "Raw invisible payload, no cover", style = MaterialTheme.typography.labelSmall, color = DimWhite.copy(alpha = 0.4f)) } }
                Switch(checked = enabled, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0B1E33), checkedTrackColor = Gold, uncheckedThumbColor = DimWhite.copy(alpha = 0.5f), uncheckedTrackColor = NavyBorder))
            }
            AnimatedVisibility(visible = enabled) { OutlinedTextField(value = text, onValueChange = onTextChange, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 14.dp).heightIn(min = 80.dp), placeholder = { Text("Custom carrier text (leave empty for auto-generated)...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 4, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = Gold)) }
        }
    }
}

@Composable private fun ModeCard(icon: ImageVector, title: String, subtitle: String, selected: Boolean, accent: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (selected) accent.copy(alpha = 0.12f) else NavyCard), border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selected) accent.copy(alpha = 0.5f) else NavyBorder)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, Modifier.size(32.dp), tint = if (selected) accent else DimWhite.copy(alpha = 0.5f)); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = if (selected) accent else DimWhite, textAlign = androidx.compose.ui.text.style.TextAlign.Center); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.labelSmall, color = DimWhite.copy(alpha = 0.45f), textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
    }
}

@Composable private fun TextModeContent(state: EncodeUiState, viewModel: EncodeViewModel) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Edit, null, Modifier.size(22.dp), tint = Gold); Spacer(Modifier.width(10.dp)); Text("Write your hidden message", style = MaterialTheme.typography.titleSmall, color = Gold) }; Spacer(Modifier.height(12.dp)); OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp), placeholder = { Text("Type the secret text to hide...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 8, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = Gold)); if (state.inputText.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text("${state.inputText.toByteArray(Charsets.UTF_8).size} bytes", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f)) } }
    }
}

@Composable private fun FileModeContent(state: EncodeUiState, viewModel: EncodeViewModel, filePicker: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Outlined.Upload, null, Modifier.size(40.dp), tint = TealAccent); Spacer(Modifier.height(10.dp)); Text("Select any file from your device", style = MaterialTheme.typography.titleSmall, color = TealAccent); Spacer(Modifier.height(4.dp)); Text("Images · Video · Audio · PDF · ZIP · APK · Any type", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.45f)); Spacer(Modifier.height(16.dp)); if (state.selectedFileName != null) { Surface(color = Color(0xFF0A2A1E), shape = RoundedCornerShape(10.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = TealAccent); Spacer(Modifier.width(10.dp)); Column { Text(state.selectedFileName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = DimWhite); Text(formatFileSize(state.selectedFileSize), style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f)) } } }; Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = { filePicker.launch(arrayOf("*/*")) }, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.5f))) { Icon(Icons.Filled.SwapHoriz, null, tint = TealAccent); Spacer(Modifier.width(8.dp)); Text("Change File", color = TealAccent) } } else { Button(onClick = { filePicker.launch(arrayOf("*/*")) }, colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Filled.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose File", fontWeight = FontWeight.SemiBold) } } }
    }
}

private fun formatFileSize(bytes: Long): String = when { bytes < 1024 -> "$bytes B"; bytes < 1024 * 1024 -> "${bytes / 1024} KB"; bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"; else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB" }
