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
fun DecodeScreen(
    onNavigateBack: () -> Unit,
    onDecodeComplete: (String) -> Unit,
    viewModel: DecodeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.result) { state.result?.let { onDecodeComplete("done") } }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Decode Text", color = DimWhite) },
                navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TealAccent) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17)),
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                state.detection?.let { detection ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (detection.hasHiddenPayload) Color(0xFF0F3D2E).copy(alpha = 0.6f) else NavyCard),
                        shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (detection.hasHiddenPayload) TealAccent.copy(alpha = 0.3f) else NavyBorder)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (detection.hasHiddenPayload) Icons.Default.CheckCircle else Icons.Default.HelpOutline, null,
                                tint = if (detection.hasHiddenPayload) TealAccent else DimWhite.copy(alpha = 0.5f))
                            Spacer(Modifier.width(12.dp))
                            Text(if (detection.hasHiddenPayload) "Hidden data detected!" else "No hidden data found",
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = if (detection.hasHiddenPayload) TealAccent else DimWhite)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(value = state.inputText, onValueChange = { viewModel.setInputText(it) }, modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                    label = { Text("Paste text containing hidden data") }, placeholder = { Text("Paste stego text here...") }, maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealAccent, unfocusedBorderColor = NavyBorder, focusedLabelColor = TealAccent, cursorColor = TealAccent))
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.decode() }, enabled = state.inputText.isNotBlank() && !state.isLoading, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent, contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(12.dp)) {
                    if (state.isLoading) { CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF00382E)); Spacer(Modifier.width(8.dp)); Text("Decoding...") }
                    else { Icon(Icons.Default.Search, null); Spacer(Modifier.width(8.dp)); Text("Extract Hidden File", fontWeight = FontWeight.SemiBold) }
                }
                state.error?.let { error ->
                    Spacer(Modifier.height(16.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
