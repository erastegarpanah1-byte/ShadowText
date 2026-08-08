package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.DecodeInputMode
import ai.zaro.shadowtext.ui.viewmodel.DecodeUiState
import ai.zaro.shadowtext.ui.viewmodel.DecodeViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FindInPage
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    Scaffold(containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text("Decode", color = DimWhite) }, navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.Filled.ArrowBack, "Back", tint = TealAccent) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))) }) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                state.result?.let { result -> DecodeResultCard(result, onDismiss = { viewModel.reset() }); return@Scaffold }
                Text("How do you want to provide the text?", style = MaterialTheme.typography.titleSmall, color = DimWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { ModeCard(Icons.Outlined.EditNote, "Paste Text", "Paste stego text directly", state.inputMode == DecodeInputMode.TEXT, TealAccent, { viewModel.setInputMode(DecodeInputMode.TEXT) }, Modifier.weight(1f)); ModeCard(Icons.Outlined.UploadFile, "Open File", "Load .txt file", state.inputMode == DecodeInputMode.FILE, Gold, { viewModel.setInputMode(DecodeInputMode.FILE) }, Modifier.weight(1f)) }
                Spacer(Modifier.height(14.dp)); state.detection?.let { d -> DetectionCard(d) }; Spacer(Modifier.height(14.dp))
                when (state.inputMode) { DecodeInputMode.TEXT -> TextInputContent(state, viewModel); DecodeInputMode.FILE -> FileInputContent(state, viewModel, filePicker) }
                Spacer(Modifier.height(14.dp))
                Button(onClick = { viewModel.decode() }, enabled = state.inputText.isNotBlank() && !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), color = Color(0xFF00382E), trackColor = TealAccent.copy(alpha = 0.3f)) } else { Icon(Icons.Outlined.FindInPage, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Extract Hidden Data", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                }
                state.error?.let { e -> Spacer(Modifier.height(14.dp)); ErrorCard(e) }; Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DecodeResultCard(result: ai.zaro.shadowtext.core.engine.DecodeResult, onDismiss: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Icon(Icons.Filled.CheckCircle, null, Modifier.size(72.dp), tint = TealAccent) }
        Spacer(Modifier.height(16.dp))
        Text("Hidden Data Extracted!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TealAccent)
        Spacer(Modifier.height(6.dp))
        Text("Payload Size: " + formatDecodeSize(result.payload.size.toLong()) + " | Type: " + result.payloadTypeLabel, style = MaterialTheme.typography.bodyMedium, color = DimWhite.copy(alpha = 0.6f))
        result.metadata["filename"]?.let { Text("File: $it", style = MaterialTheme.typography.bodySmall, color = Gold); Spacer(Modifier.height(8.dp)) }
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.3f))) {
            Column(Modifier.padding(16.dp)) {
                Text("Decoded Content", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = TealAccent)
                Spacer(Modifier.height(8.dp))
                val text = try { String(result.payload, Charsets.UTF_8).take(2000) } catch (_: Exception) { result.payload.take(256).joinToString(" ") { java.lang.String.format("%02X", it) } }
                Text(text, style = MaterialTheme.typography.bodySmall, color = DimWhite)
            }
        }
        Spacer(Modifier.height(20.dp))
        if (result.metadata.isNotEmpty()) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.5f)), shape = RoundedCornerShape(10.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Metadata", style = MaterialTheme.typography.labelMedium, color = DimWhite.copy(alpha = 0.5f))
                    Spacer(Modifier.height(6.dp))
                    result.metadata.forEach { (k, v) -> if (v.isNotEmpty()) Text("$k: $v", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.6f)) }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        OutlinedButton(onClick = onDismiss, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.4f))) { Text("Decode Another", color = TealAccent) }
    }
}
