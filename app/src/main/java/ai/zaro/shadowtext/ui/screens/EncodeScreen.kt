package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.EncodeViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val Gold = Color(0xFFD4A574)
private val DimWhite = Color(0xFFC1C6CF)
private val NavyCard = Color(0xFF111D30)
private val NavyBorder = Color(0xFF1E3050)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(
    onNavigateBack: () -> Unit,
    onEncodeComplete: (String) -> Unit,
    viewModel: EncodeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.loadFile(it) }
    }

    LaunchedEffect(state.result) { state.result?.let { onEncodeComplete(it.stegoText) } }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Encode File", color = DimWhite) },
                navigationIcon = { IconButton(onClick = { viewModel.reset(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17)),
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.UploadFile, null, Modifier.size(48.dp), tint = Gold)
                        Spacer(Modifier.height(12.dp))
                        Text("Step 1: Select a file to hide", style = MaterialTheme.typography.titleSmall, color = DimWhite)
                        if (state.selectedFileName != null) {
                            Spacer(Modifier.height(12.dp))
                            Surface(color = Color(0xFF2A1F10), shape = RoundedCornerShape(8.dp)) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Gold)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(state.selectedFileName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = DimWhite)
                                        Text(formatFileSize(state.selectedFileSize), style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { filePicker.launch(arrayOf("*/*")) }, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF0B1E33)), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose File", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, null, Modifier.size(48.dp), tint = Gold)
                        Spacer(Modifier.height(12.dp))
                        Text("Step 2: Generate Stego Text", style = MaterialTheme.typography.titleSmall, color = DimWhite)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.encode() }, enabled = state.bytesLoaded && !state.isLoading, modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED4B4), contentColor = Color(0xFF00382E)), shape = RoundedCornerShape(12.dp)) {
                            if (state.isLoading) { CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF00382E)); Spacer(Modifier.width(8.dp)); Text("Encoding...") }
                            else { Icon(Icons.Default.Lock, null); Spacer(Modifier.width(8.dp)); Text("Encode to Stego Text", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
                state.error?.let { error ->
                    Spacer(Modifier.height(16.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1A1A)), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFFB4AB)); Spacer(Modifier.width(8.dp)); Text(error, color = Color(0xFFFFDAD4), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
                Text("The stego text looks like ordinary sentences but contains invisible Unicode characters encoding your file.", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.4f))
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
