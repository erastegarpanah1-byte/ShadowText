package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.EncodeMode
import ai.zaro.shadowtext.ui.viewmodel.EncodeViewModel
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
        topBar = { TopAppBar(title = { Text("Encode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Gold) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("What do you want to hide?", style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModeCard(Icons.Outlined.TextFields, "Hidden Message", "Type a secret text", state.mode == EncodeMode.TEXT, Gold, { viewModel.setMode(EncodeMode.TEXT) }, Modifier.weight(1f))
                    ModeCard(Icons.Outlined.InsertDriveFile, "Hidden File", "Images, video, PDF, etc.", state.mode == EncodeMode.FILE, TealAccent, { viewModel.setMode(EncodeMode.FILE) }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                when (state.mode) {
                    EncodeMode.TEXT -> TextModeContent(state, viewModel)
                    EncodeMode.FILE -> FileModeContent(state, viewModel, filePicker)
                }
                Spacer(Modifier.height(16.dp))
                val canEncode = when (state.mode) { EncodeMode.TEXT -> state.inputText.isNotBlank(); EncodeMode.FILE -> state.bytesLoaded }
                Button(onClick = { viewModel.encode() }, enabled = canEncode && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color(0xFF00382E)); Spacer(Modifier.width(10.dp)); Text("Encoding...") }
                    else { Icon(Icons.Filled.Lock, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Encode Now", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                }
                state.error?.let { error ->
                    Spacer(Modifier.height(14.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4), style = MaterialTheme.typography.bodyMedium) }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("Your data will be hidden inside normal-looking text using invisible characters.", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.35f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ModeCard(icon: ImageVector, title: String, subtitle: String, selected: Boolean, accent: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) accent.copy(alpha = 0.12f) else NavyCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selected) accent.copy(alpha = 0.5f) else NavyBorder)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(32.dp), tint = if (selected) accent else DimWhite.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = if (selected) accent else DimWhite, textAlign = TextAlign.Center)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = DimWhite.copy(alpha = 0.45f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TextModeContent(state: EncodeUiState, viewModel: EncodeViewModel) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Edit, null, Modifier.size(22.dp), tint = Gold); Spacer(Modifier.width(10.dp)); Text("Write your hidden message", style = MaterialTheme.typography.titleSmall, color = Gold) }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp), placeholder = { Text("Type the secret text to hide...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = Gold))
            if (state.inputText.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text("${state.inputText.toByteArray(Charsets.UTF_8).size} bytes", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f)) }
        }
    }
}

@Composable
private fun FileModeContent(state: EncodeUiState, viewModel: EncodeViewModel, filePicker: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Upload, null, Modifier.size(40.dp), tint = TealAccent)
            Spacer(Modifier.height(10.dp))
            Text("Select any file from your device", style = MaterialTheme.typography.titleSmall, color = TealAccent)
            Spacer(Modifier.height(4.dp))
            Text("Images · Video · Audio · PDF · ZIP · APK · Any type", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.45f))
            Spacer(Modifier.height(16.dp))
            if (state.selectedFileName != null) {
                Surface(color = Color(0xFF0A2A1E), shape = RoundedCornerShape(10.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = TealAccent); Spacer(Modifier.width(10.dp)); Column { Text(state.selectedFileName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = DimWhite); Text(formatFileSize(state.selectedFileSize), style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f)) } }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { filePicker.launch(arrayOf("*/*")) }, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.5f))) { Icon(Icons.Filled.SwapHoriz, null, tint = TealAccent); Spacer(Modifier.width(8.dp)); Text("Change File", color = TealAccent) }
            } else {
                Button(onClick = { filePicker.launch(arrayOf("*/*")) }, colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Filled.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose File", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
    else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
}
