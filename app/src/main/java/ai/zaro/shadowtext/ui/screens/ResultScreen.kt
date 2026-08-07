package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.viewmodel.ResultViewModel
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private val Gold = Color(0xFFD4A574)
private val TealAccent = Color(0xFF2ED4B4)
private val DimWhite = Color(0xFFC1C6CF)
private val NavyCard = Color(0xFF111D30)
private val NavyBorder = Color(0xFF1E3050)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(mode: String, stegoText: String, onNavigateBack: () -> Unit, onNavigateHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val accent = if (mode == "encoded") Gold else TealAccent

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(if (mode == "encoded") "Encoding Complete" else "Decoding Complete", color = DimWhite) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = accent) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17)),
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), Color(0xFF0D1625), Color(0xFF0A1A2E))))) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Icon(Icons.Default.CheckCircle, null, Modifier.size(72.dp), tint = accent) }
                Spacer(Modifier.height(16.dp))
                Text(if (mode == "encoded") "File successfully hidden in text!" else "Hidden file extracted!",
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DimWhite)
                if (mode == "encoded" && stegoText.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = NavyCard), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Stego Text Preview", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = DimWhite)
                            Spacer(Modifier.height(8.dp))
                            Text(stegoText.take(500) + if (stegoText.length > 500) "..." else "", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.7f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { clipboardManager.setText(AnnotatedString(stegoText)); scope.launch { snackbarHostState.showSnackbar("Copied!") } },
                            Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color(0xFF0B1E33)), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.ContentCopy, null); Spacer(Modifier.width(8.dp)); Text("Copy", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(onClick = {
                            scope.launch { try { context.startActivity(android.content.Intent.createChooser(viewModel.shareStegoText(stegoText), "Share")) } catch (_: Exception) { snackbarHostState.showSnackbar("Share failed") } }
                        }, Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, accent)) {
                            Icon(Icons.Default.Share, null, tint = accent); Spacer(Modifier.width(8.dp)); Text("Share", color = accent)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3D2E1E).copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Info, null, Modifier.size(20.dp), tint = Gold); Spacer(Modifier.width(8.dp))
                            Text("Some apps strip invisible characters. Test your target platform first.", style = MaterialTheme.typography.bodySmall, color = Gold)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = onNavigateHome, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)) {
                    Icon(Icons.Default.Home, null, tint = DimWhite); Spacer(Modifier.width(8.dp)); Text("Back to Home", color = DimWhite)
                }
            }
        }
    }
}
