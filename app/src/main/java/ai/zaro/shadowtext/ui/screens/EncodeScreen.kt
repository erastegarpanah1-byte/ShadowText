package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.EncodeMode
import ai.zaro.shadowtext.ui.viewmodel.EncodeUiState
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
        topBar = { TopAppBar(title = { Text("Encode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.Filled.ArrowBack, "Back", tint = Gold) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("What do you want to hide?", style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModeCard(Icons.Outlined.TextFields, "Message in Text", "Hide a secret message inside a cover text", state.mode == EncodeMode.TEXT, Gold, { viewModel.setMode(EncodeMode.TEXT) }, Modifier.weight(1f))
                    ModeCard(Icons.Outlined.InsertDriveFile, "File in Text", "Hide a file inside a cover text", state.mode == EncodeMode.FILE, TealAccent, { viewModel.setMode(EncodeMode.FILE) }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                SectionHeader("1. Secret Content", Icons.Outlined.Lock, Gold)
                Spacer(Modifier.height(10.dp))
                when (state.mode) {
                    EncodeMode.TEXT -> SecretTextField(value = state.secretText, onValueChange = { viewModel.setSecretText(it) }, accent = Gold)
                    EncodeMode.FILE -> FilePickerArea(fileName = state.selectedFileName, fileSize = state.selectedFileSize, isLoading = state.isLoading, onPick = { filePicker.launch(arrayOf("*/*")) })
                }
                Spacer(Modifier.height(20.dp))
                SectionHeader("2. Cover Text", Icons.Outlined.Article, TealAccent)
                Spacer(Modifier.height(6.dp))
                Text("This is the visible text others will see. Your secret is hidden inside.", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = state.carrierText, onValueChange = { viewModel.setCarrierText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), placeholder = { Text("Enter a cover text...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 6, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealAccent.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = TealAccent))
                Spacer(Modifier.height(24.dp))
                val canEncode = when (state.mode) { EncodeMode.TEXT -> state.secretText.isNotBlank() && state.carrierText.isNotBlank(); EncodeMode.FILE -> state.bytesLoaded && state.carrierText.isNotBlank() }
                Button(onClick = { viewModel.encode() }, enabled = canEncode && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E), disabledContainerColor = TealAccent.copy(alpha = 0.3f), disabledContentColor = TealAccent.copy(alpha = 0.5f)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), color = Color(0xFF00382E), trackColor = TealAccent.copy(alpha = 0.3f)) } else { Icon(Icons.Filled.Lock, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Encode Now", fontWeight = FontWeight.Bold) }
                }
                state.error?.let { Spacer(Modifier.height(14.dp)); ErrorCard(it) }; Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable private fun SectionHeader(title: String, icon: ImageVector, accent: Color) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, Modifier.size(20.dp), tint = accent); Spacer(Modifier.width(8.dp)); Text(title, style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold) } }

@Composable private fun SecretTextField(value: String, onValueChange: (String) -> Unit, accent: Color) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Edit, null, Modifier.size(22.dp), tint = accent); Spacer(Modifier.width(10.dp)); Text("Secret Message", style = MaterialTheme.typography.titleSmall, color = accent) }; Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp), placeholder = { Text("Type the secret message to hide...", color = DimWhite.copy(alpha = 0.3f)) }, maxLines = 8, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent.copy(alpha = 0.5f), unfocusedBorderColor = NavyBorder, focusedTextColor = DimWhite, unfocusedTextColor = DimWhite, cursorColor = accent))
            if (value.isNotBlank()) { Spacer(Modifier.height(6.dp)); Text("${value.toByteArray(Charsets.UTF_8).size} bytes", style = MaterialTheme.typography.labelSmall, color = accent.copy(alpha = 0.5f)) }
        }
    }
}

@Composable private fun FilePickerArea(fileName: String?, fileSize: Long, isLoading: Boolean, onPick: () -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.25f))) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Upload, null, Modifier.size(40.dp), tint = TealAccent); Spacer(Modifier.height(10.dp))
            Text("Select any file from your device", style = MaterialTheme.typography.titleSmall, color = TealAccent); Spacer(Modifier.height(4.dp))
            Text("Images | Video | Audio | PDF | ZIP | APK | Any type", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.45f)); Spacer(Modifier.height(16.dp))
            if (fileName != null) {
                Surface(color = Color(0xFF0A2A1E), shape = RoundedCornerShape(10.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = TealAccent); Spacer(Modifier.width(10.dp)); Column { Text(fileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = DimWhite); Text(formatFileSize(fileSize), style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f)) } } }
                Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = onPick, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.5f))) { Icon(Icons.Filled.SwapHoriz, null, tint = TealAccent); Spacer(Modifier.width(8.dp)); Text("Change File", color = TealAccent) }
            } else {
                Button(onClick = onPick, colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(12.dp), enabled = !isLoading) { Icon(Icons.Filled.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose File", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable private fun ModeCard(icon: ImageVector, title: String, subtitle: String, selected: Boolean, accent: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (selected) accent.copy(alpha = 0.12f) else NavyCard), border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selected) accent.copy(alpha = 0.5f) else NavyBorder)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, Modifier.size(32.dp), tint = if (selected) accent else DimWhite.copy(alpha = 0.5f)); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = if (selected) accent else DimWhite, textAlign = TextAlign.Center); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.labelSmall, color = DimWhite.copy(alpha = 0.45f), textAlign = TextAlign.Center) }
    }
}

@Composable private fun ErrorCard(error: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4)) } } }

private fun formatFileSize(bytes: Long): String = when { bytes < 1024 -> "$bytes B"; bytes < 1024 * 1024 -> "${bytes / 1024} KB"; bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"; else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB" }
