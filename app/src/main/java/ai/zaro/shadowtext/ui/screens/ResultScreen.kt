package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.domain.usecase.SaveAndShareUseCase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    mode: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: SaveAndShareUseCase = hiltViewModel(),
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedStateHandle = androidx.lifecycle.compose.LocalSavedStateHandle.current
    val encodeResult = if (mode == "encoded") {
        savedStateHandle.get<EncodeResult>("encodeResult")
    } else null
    val decodeResult = if (mode == "decoded") {
        savedStateHandle.get<DecodeResult>("decodeResult")
    } else null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(if (mode == "encoded") "Encoding Complete" else "Decoding Complete")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (mode == "encoded" && encodeResult != null) {
                EncodedResultContent(
                    result = encodeResult,
                    onCopyText = {
                        clipboardManager.setText(AnnotatedString(encodeResult.stegoText))
                        scope.launch {
                            snackbarHostState.showSnackbar("Stego text copied to clipboard")
                        }
                    },
                    onShareText = {
                        scope.launch {
                            try {
                                val intent = viewModel.saveStegoText(
                                    encodeResult.stegoText,
                                    "shadowtext_encoded"
                                )
                                context.startActivity(
                                    android.content.Intent.createChooser(intent, "Share Stego Text")
                                )
                            } catch (e: Exception) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Share failed: ${e.message}")
                                }
                            }
                        }
                    },
                )
            } else if (mode == "decoded" && decodeResult != null) {
                DecodedResultContent(
                    result = decodeResult,
                    onSaveFile = {
                        scope.launch {
                            try {
                                val intent = viewModel(
                                    decodeResult.payload,
                                    decodeResult.metadata["filename"],
                                    decodeResult.metadata["mimeType"],
                                )
                                context.startActivity(
                                    android.content.Intent.createChooser(intent, "Save Decoded File")
                                )
                            } catch (e: Exception) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Save failed: ${e.message}")
                                }
                            }
                        }
                    },
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No result available")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Home")
            }
        }
    }
}

@Composable
private fun EncodedResultContent(
    result: EncodeResult,
    onCopyText: () -> Unit,
    onShareText: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "File successfully hidden in text!",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatRow("Visible text", "${result.visibleText.length} chars")
            StatRow("Invisible chars", "${result.invisibleCharCount}")
            StatRow("Hidden payload", formatFileSizeRes(result.payloadSizeBytes.toLong()))
            StatRow("Encoding", result.encodingScheme)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Stego Text Preview",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.stegoText.take(500) + if (result.stegoText.length > 500) "..." else "",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Default,
                    lineHeight = 20.sp,
                ),
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onCopyText,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy")
        }
        OutlinedButton(
            onClick = onShareText,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
        ),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Some apps strip invisible characters. Test your target platform before relying on this.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun DecodedResultContent(
    result: DecodeResult,
    onSaveFile: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Hidden file extracted!",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatRow("File type", result.payloadTypeLabel)
            StatRow("Payload size", formatFileSizeRes(result.payload.size.toLong()))
            StatRow("Encoding", result.encodingScheme)

            result.metadata["filename"]?.let {
                StatRow("Original name", it)
            }
            result.metadata["mimeType"]?.let {
                StatRow("MIME type", it)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (result.metadata.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Metadata",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                result.metadata.forEach { (key, value) ->
                    if (key !in listOf("filename", "mimeType", "encodingScheme")) {
                        Text(
                            text = "$key: $value",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    Button(
        onClick = onSaveFile,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Default.SaveAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save / Share File")
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun formatFileSizeRes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
        else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
    }
}
