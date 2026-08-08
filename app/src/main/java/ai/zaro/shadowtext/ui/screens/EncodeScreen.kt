package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.ShadoColors
import ai.zaro.shadowtext.ui.ShadoDimens
import ai.zaro.shadowtext.ui.viewmodel.EncodeMode
import ai.zaro.shadowtext.ui.viewmodel.EncodeUiState
import ai.zaro.shadowtext.ui.viewmodel.EncodeViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

data class InputType(val id: String, val label: String, val icon: ImageVector, val mode: EncodeMode, val mimeTypes: Array<String>)

private val InputTypes = listOf(
    InputType("text", "Text", Icons.Outlined.TextFields, EncodeMode.TEXT, arrayOf("text/*")),
    InputType("image", "Image", Icons.Outlined.Image, EncodeMode.FILE, arrayOf("image/*")),
    InputType("video", "Video", Icons.Outlined.Videocam, EncodeMode.FILE, arrayOf("video/*")),
    InputType("audio", "Audio", Icons.Outlined.Audiotrack, EncodeMode.FILE, arrayOf("audio/*")),
    InputType("pdf", "PDF", Icons.Outlined.PictureAsPdf, EncodeMode.FILE, arrayOf("application/pdf")),
    InputType("file", "File", Icons.Outlined.InsertDriveFile, EncodeMode.FILE, arrayOf("*/*")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeScreen(
    onNavigateBack: () -> Unit,
    onEncodeComplete: (String) -> Unit,
    viewModel: EncodeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.loadFile(it) }
    }

    LaunchedEffect(state.result) {
        state.result?.let { onEncodeComplete(it.stegoText) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Encode", color = ShadoColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.reset(); onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ShadoColors.Accent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadoColors.BgDarker)
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(ShadoColors.BgDarker, ShadoColors.BgDark, ShadoColors.BgDarker))
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = ShadoDimens.paddingScreen)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(4.dp))

                // ═══ Step 1: Select Input ═══
                SectionHeader("1. Select Input", Icons.Outlined.Input)
                Spacer(Modifier.height(10.dp))

                // Input type grid — 3 columns
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in InputTypes.chunked(3)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (type in row) {
                                InputTypeCard(
                                    type = type,
                                    selected = when (type.mode) {
                                        EncodeMode.TEXT -> state.mode == EncodeMode.TEXT
                                        EncodeMode.FILE -> state.mode == EncodeMode.FILE && state.selectedFileName != null
                                    },
                                    fileName = state.selectedFileName,
                                    onClick = {
                                        when (type.mode) {
                                            EncodeMode.TEXT -> viewModel.setMode(EncodeMode.TEXT)
                                            EncodeMode.FILE -> {
                                                viewModel.setMode(EncodeMode.FILE)
                                                filePicker.launch(type.mimeTypes)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space
                            repeat(3 - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Show selected file info or text input
                Spacer(Modifier.height(14.dp))
                when (state.mode) {
                    EncodeMode.TEXT -> TextModeContent(state, viewModel)
                    EncodeMode.FILE -> {
                        if (state.selectedFileName != null) {
                            SelectedFileCard(state)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ═══ Step 2: Carrier Text ═══
                SectionHeader("2. Carrier Text", Icons.Outlined.Article)
                Spacer(Modifier.height(10.dp))
                CarrierTextSection(state, viewModel)

                Spacer(Modifier.height(16.dp))

                // ═══ Step 3: Options ═══
                SectionHeader("3. Options", Icons.Outlined.Tune)
                Spacer(Modifier.height(10.dp))
                OptionsCard()

                Spacer(Modifier.height(20.dp))

                // ═══ Encode Button ═══
                val canEncode = when (state.mode) {
                    EncodeMode.TEXT -> state.inputText.isNotBlank()
                    EncodeMode.FILE -> state.bytesLoaded
                }

                Button(
                    onClick = { viewModel.encode() },
                    enabled = canEncode && !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShadoDimens.btnHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShadoColors.Accent,
                        contentColor = Color(0xFF001F2B),
                        disabledContainerColor = ShadoColors.Accent.copy(alpha = 0.3f),
                        disabledContentColor = ShadoColors.Accent.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(ShadoDimens.cornerMd)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF001F2B)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Encoding...")
                    } else {
                        Icon(Icons.Filled.Lock, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Encode", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }

                // Error
                state.error?.let { error ->
                    Spacer(Modifier.height(12.dp))
                    ErrorBanner(error)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ═══ Section Header ═══
@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(18.dp), tint = ShadoColors.Accent)
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 0.5.sp),
            color = ShadoColors.TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ═══ Input Type Card ═══
@Composable
private fun InputTypeCard(
    type: InputType,
    selected: Boolean,
    fileName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (type.id == "text") ShadoColors.Accent else ShadoColors.Gold

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(ShadoDimens.cornerMd))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) accentColor.copy(alpha = 0.1f) else ShadoColors.BgCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (selected) accentColor.copy(alpha = 0.5f) else ShadoColors.BorderSubtle
        )
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                type.icon,
                null,
                Modifier.size(28.dp),
                tint = if (selected) accentColor else ShadoColors.TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Text(
                type.label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) accentColor else ShadoColors.TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ═══ Text Mode Content ═══
@Composable
private fun TextModeContent(state: EncodeUiState, viewModel: EncodeViewModel) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Accent.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Edit, null, Modifier.size(20.dp), tint = ShadoColors.Accent)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Write your hidden message",
                    style = MaterialTheme.typography.titleSmall,
                    color = ShadoColors.Accent
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.inputText,
                onValueChange = { viewModel.setInputText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text(
                        "Type the secret text to hide...",
                        color = ShadoColors.TextDisabled
                    )
                },
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShadoColors.Accent.copy(alpha = 0.5f),
                    unfocusedBorderColor = ShadoColors.BorderSubtle,
                    focusedTextColor = ShadoColors.TextPrimary,
                    unfocusedTextColor = ShadoColors.TextPrimary,
                    cursorColor = ShadoColors.Accent
                )
            )
            if (state.inputText.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "${state.inputText.toByteArray(Charsets.UTF_8).size} bytes",
                    style = MaterialTheme.typography.labelSmall,
                    color = ShadoColors.Accent.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ═══ Selected File Card ═══
@Composable
private fun SelectedFileCard(state: EncodeUiState) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadoColors.SuccessBg),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Success.copy(alpha = 0.3f))
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CheckCircle, null, Modifier.size(22.dp), tint = ShadoColors.Success)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    state.selectedFileName ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ShadoColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    formatFileSize(state.selectedFileSize),
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadoColors.Success.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ═══ Carrier Text Section ═══
@Composable
private fun CarrierTextSection(state: EncodeUiState, viewModel: EncodeViewModel) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleCarrierText() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    Icons.Outlined.Article,
                    null,
                    Modifier.size(22.dp),
                    tint = if (state.useCarrierText) ShadoColors.Accent else ShadoColors.TextDisabled
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "Carrier Text",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.useCarrierText) ShadoColors.TextPrimary else ShadoColors.TextSecondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        if (state.useCarrierText) "Embed payload in visible text"
                        else "Raw invisible payload only",
                        style = MaterialTheme.typography.labelSmall,
                        color = ShadoColors.TextMuted
                    )
                }
            }
            Switch(
                checked = state.useCarrierText,
                onCheckedChange = { viewModel.toggleCarrierText() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ShadoColors.BgDark,
                    checkedTrackColor = ShadoColors.Accent,
                    uncheckedThumbColor = ShadoColors.TextMuted,
                    uncheckedTrackColor = ShadoColors.Border
                )
            )
        }

        AnimatedVisibility(
            visible = state.useCarrierText,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            OutlinedTextField(
                value = state.carrierText,
                onValueChange = { viewModel.setCarrierText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                    .heightIn(min = 80.dp),
                placeholder = {
                    Text(
                        "Enter custom carrier text (or leave empty for auto)...",
                        color = ShadoColors.TextDisabled
                    )
                },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShadoColors.Accent.copy(alpha = 0.4f),
                    unfocusedBorderColor = ShadoColors.BorderSubtle,
                    focusedTextColor = ShadoColors.TextPrimary,
                    unfocusedTextColor = ShadoColors.TextPrimary,
                    cursorColor = ShadoColors.Accent
                )
            )
        }
    }
}

// ═══ Options Card ═══
@Composable
private fun OptionsCard() {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)
    ) {
        Column(Modifier.padding(16.dp)) {
            OptionsRow(
                label = "Encoding Mode",
                value = "ZWC",
                icon = Icons.Outlined.Code
            )
            HorizontalDivider(color = ShadoColors.BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
            OptionsRow(
                label = "Compression",
                value = "Enabled",
                icon = Icons.Outlined.Compress,
                valueColor = ShadoColors.Success
            )
            HorizontalDivider(color = ShadoColors.BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
            OptionsRow(
                label = "Encrypt (Phase 2)",
                value = "Coming Soon",
                icon = Icons.Outlined.Shield,
                valueColor = ShadoColors.TextMuted
            )
        }
    }
}

@Composable
private fun OptionsRow(
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = ShadoColors.TextSecondary
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(18.dp), tint = ShadoColors.TextMuted)
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = ShadoColors.TextSecondary
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// ═══ Error Banner ═══
@Composable
private fun ErrorBanner(error: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ShadoColors.ErrorBg),
        shape = RoundedCornerShape(ShadoDimens.cornerSm)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, null, Modifier.size(20.dp), tint = ShadoColors.Error)
            Spacer(Modifier.width(8.dp))
            Text(error, color = ShadoColors.Error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatFileSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
    else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
}
