package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.ui.ShadoColors
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeScreen(
    onNavigateBack: () -> Unit,
    onDecodeComplete: (String) -> Unit,
    viewModel: DecodeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.loadFileForDecode(it) }
    }
    LaunchedEffect(state.result) { state.result?.let { onDecodeComplete("done") } }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Decode", color = ShadoColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ShadoColors.Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadoColors.BgDarker)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ShadoColors.BgDarker, ShadoColors.BgDark, ShadoColors.BgDarker)))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(4.dp))
                Text("Input Method", style = MaterialTheme.typography.titleSmall, color = ShadoColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DecodeModeBtn(Icons.Outlined.EditNote, "Paste Text", state.inputMode == DecodeInputMode.TEXT, ShadoColors.Accent, { viewModel.setInputMode(DecodeInputMode.TEXT) }, Modifier.weight(1f))
                    DecodeModeBtn(Icons.Outlined.UploadFile, "Open File", state.inputMode == DecodeInputMode.FILE, ShadoColors.Gold, { viewModel.setInputMode(DecodeInputMode.FILE) }, Modifier.weight(1f))
                }
                state.detection?.let { d -> Spacer(Modifier.height(12.dp)); DetectionBanner(d) }
                Spacer(Modifier.height(12.dp))
                when (state.inputMode) {
                    DecodeInputMode.TEXT -> PasteArea(state, viewModel)
                    DecodeInputMode.FILE -> FileInputArea(state, viewModel, filePicker)
                }
                Spacer(Modifier.height(12.dp))
                OptionLabel("Options", Icons.Outlined.Tune)
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)) {
                    Column(Modifier.padding(16.dp)) {
                        OpRow("Auto Detect", "On", Icons.Outlined.Search, ShadoColors.Success)
                        HorizontalDivider(color = ShadoColors.BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
                        OpRow("Encoding Mode", "ZWC", Icons.Outlined.Code)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.decode() }, enabled = state.inputText.isNotBlank() && !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShadoColors.Gold, contentColor = Color(0xFF1A0A00), disabledContainerColor = ShadoColors.Gold.copy(alpha = 0.3f), disabledContentColor = ShadoColors.Gold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (state.isLoading) { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color(0xFF1A0A00)); Spacer(Modifier.width(10.dp)); Text("Extracting...") }
                    else { Icon(Icons.Outlined.FindInPage, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Decode", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
                }
                state.error?.let { e -> Spacer(Modifier.height(12.dp)); ErrBanner(e) }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable private fun OptionLabel(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, Modifier.size(18.dp), tint = ShadoColors.Accent); Spacer(Modifier.width(8.dp)); Text(title, style = MaterialTheme.typography.titleSmall, color = ShadoColors.TextPrimary, fontWeight = FontWeight.SemiBold) }
}
@Composable private fun DecodeModeBtn(icon: ImageVector, title: String, sel: Boolean, accent: Color, onClick: () -> Unit, mod: Modifier) {
    Card(mod.clickable(onClick = onClick), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (sel) accent.copy(alpha = 0.1f) else ShadoColors.BgCard), border = androidx.compose.foundation.BorderStroke(1.5.dp, if (sel) accent.copy(alpha = 0.5f) else ShadoColors.BorderSubtle)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, Modifier.size(24.dp), tint = if (sel) accent else ShadoColors.TextMuted); Spacer(Modifier.width(10.dp)); Text(title, style = MaterialTheme.typography.labelMedium, color = if (sel) accent else ShadoColors.TextSecondary, fontWeight = FontWeight.SemiBold)
        }
    }
}
@Composable private fun DetectionBanner(d: DetectionResult) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (d.hasHiddenPayload) ShadoColors.SuccessBg else ShadoColors.BgCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (d.hasHiddenPayload) ShadoColors.Success.copy(alpha = 0.3f) else ShadoColors.BorderSubtle)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (d.hasHiddenPayload) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff, null, Modifier.size(24.dp), tint = if (d.hasHiddenPayload) ShadoColors.Success else ShadoColors.TextMuted)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(if (d.hasHiddenPayload) "Hidden data detected!" else "No hidden data detected", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = if (d.hasHiddenPayload) ShadoColors.Success else ShadoColors.TextSecondary)
                if (d.hasHiddenPayload) { d.payloadSizeBytes.takeIf { it > 0 }?.let { Text("Size: ${formatDs(it.toLong())}", style = MaterialTheme.typography.bodySmall, color = ShadoColors.Success.copy(alpha = 0.7f)) }; d.encodingScheme?.let { Text("Scheme: $it", style = MaterialTheme.typography.bodySmall, color = ShadoColors.TextMuted) } }
            }
        }
    }
}
@Composable private fun PasteArea(state: DecodeUiState, viewModel: DecodeViewModel) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Accent.copy(alpha = 0.2f))) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.EditNote, null, Modifier.size(20.dp), tint = ShadoColors.Accent); Spacer(Modifier.width(8.dp)); Text("Paste encoded text", style = MaterialTheme.typography.labelMedium, color = ShadoColors.Accent) }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp), placeholder = { Text("Paste encoded text here...", color = ShadoColors.TextDisabled) }, maxLines = 10, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShadoColors.Accent.copy(alpha = 0.5f), unfocusedBorderColor = ShadoColors.BorderSubtle, focusedTextColor = ShadoColors.TextPrimary, unfocusedTextColor = ShadoColors.TextPrimary, cursorColor = ShadoColors.Accent))
        }
    }
}
@Composable private fun FileInputArea(state: DecodeUiState, vm: DecodeViewModel, filePicker: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard), shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Gold.copy(alpha = 0.2f))) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Description, null, Modifier.size(40.dp), tint = ShadoColors.Gold); Spacer(Modifier.height(10.dp))
            Text("Select a text file containing stego text", style = MaterialTheme.typography.titleSmall, color = ShadoColors.Gold)
            Spacer(Modifier.height(4.dp)); Text("Supports .txt, .text, .sms, or any plain-text file", style = MaterialTheme.typography.bodySmall, color = ShadoColors.TextSecondary)
            Spacer(Modifier.height(16.dp))
            if (state.selectedFileName != null) {
                Surface(color = ShadoColors.BgSurface, shape = RoundedCornerShape(10.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = ShadoColors.Gold); Spacer(Modifier.width(10.dp)); Text(state.selectedFileName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ShadoColors.TextPrimary) } }
                if (state.inputText.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(state.inputText.take(200) + if (state.inputText.length > 200) "..." else "", style = MaterialTheme.typography.bodySmall, color = ShadoColors.TextSecondary, modifier = Modifier.padding(horizontal = 8.dp)) }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { filePicker.launch(arrayOf("text/*", "application/octet-stream")) }, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Gold.copy(alpha = 0.5f))) { Icon(Icons.Filled.SwapHoriz, null, tint = ShadoColors.Gold); Spacer(Modifier.width(8.dp)); Text("Change File", color = ShadoColors.Gold) }
            } else {
                Button(onClick = { filePicker.launch(arrayOf("text/*", "application/octet-stream")) }, colors = ButtonDefaults.buttonColors(containerColor = ShadoColors.Gold, contentColor = Color(0xFF1A0A00)), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Filled.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose Text File", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
@Composable private fun OpRow(label: String, value: String, icon: ImageVector, valueColor: Color = ShadoColors.TextSecondary) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, Modifier.size(18.dp), tint = ShadoColors.TextMuted); Spacer(Modifier.width(10.dp)); Text(label, style = MaterialTheme.typography.bodyMedium, color = ShadoColors.TextSecondary) }
        Text(value, style = MaterialTheme.typography.labelMedium, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
@Composable private fun ErrBanner(error: String) {
    Card(colors = CardDefaults.cardColors(containerColor = ShadoColors.ErrorBg), shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = ShadoColors.Error); Spacer(Modifier.width(8.dp)); Text(error, color = ShadoColors.Error, style = MaterialTheme.typography.bodyMedium) }
    }
}
private fun formatDs(bytes: Long): String = when { bytes < 1024 -> "$bytes B"; bytes < 1024 * 1024 -> "${bytes / 1024} KB"; else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB" }
