package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.ui.ShadoColors
import ai.zaro.shadowtext.ui.ShadoDimens
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
import androidx.compose.material.icons.outlined.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    mode: String,
    stegoText: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isEncoded = mode == "encoded"
    val accent = if (isEncoded) ShadoColors.Accent else ShadoColors.Gold

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(if (isEncoded) "Encode Result" else "Decode Result", color = ShadoColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = accent) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadoColors.BgDarker)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ShadoColors.BgDarker, ShadoColors.BgDark, ShadoColors.BgDarker)))) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = ShadoDimens.paddingScreen).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                // Success Icon
                Box(Modifier.size(80.dp).background(accent.copy(alpha = 0.08f), RoundedCornerShape(50)).border(1.5.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(48.dp), tint = accent)
                }

                Spacer(Modifier.height(20.dp))
                Text("Success", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ShadoColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (isEncoded) "Your data has been encoded" else "Hidden data extracted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ShadoColors.TextSecondary
                )

                Spacer(Modifier.height(24.dp))

                // Encode: Preview + Actions
                if (isEncoded && stegoText.isNotEmpty()) {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
                        shape = RoundedCornerShape(ShadoDimens.cornerMd),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Preview", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = ShadoColors.Accent)
                            Spacer(Modifier.height(10.dp))
                            Text(stegoText.take(500) + if (stegoText.length > 500) "..." else "", style = MaterialTheme.typography.bodySmall, color = ShadoColors.TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { clipboardManager.setText(AnnotatedString(stegoText)); scope.launch { snackbarHostState.showSnackbar("Copied!") } },
                            modifier = Modifier.fillMaxWidth().height(ShadoDimens.btnHeight),
                            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color(0xFF001F2B)),
                            shape = RoundedCornerShape(ShadoDimens.cornerMd)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Copy", fontWeight = FontWeight.SemiBold)
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { scope.launch { try { context.startActivity(android.content.Intent.createChooser(viewModel.shareStegoText(stegoText), "Share")) } catch (_: Exception) { snackbarHostState.showSnackbar("Share failed") } } },
                                modifier = Modifier.weight(1f).height(ShadoDimens.btnHeight),
                                shape = RoundedCornerShape(ShadoDimens.cornerMd),
                                border = androidx.compose.foundation.BorderStroke(1.dp, accent)
                            ) { Icon(Icons.Default.Share, null, tint = accent); Spacer(Modifier.width(8.dp)); Text("Share", color = accent) }

                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(ShadoDimens.btnHeight),
                                shape = RoundedCornerShape(ShadoDimens.cornerMd),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Border)
                            ) { Icon(Icons.Outlined.SaveAlt, null, tint = ShadoColors.TextSecondary); Spacer(Modifier.width(8.dp)); Text("Save", color = ShadoColors.TextSecondary) }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Warning
                    Card(colors = CardDefaults.cardColors(containerColor = ShadoColors.WarningBg.copy(alpha = 0.5f)), shape = RoundedCornerShape(ShadoDimens.cornerSm)) {
                        Row(Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Info, null, Modifier.size(18.dp), tint = ShadoColors.Warning)
                            Spacer(Modifier.width(8.dp))
                            Text("Some apps strip invisible characters. Test your target platform first.", style = MaterialTheme.typography.bodySmall, color = ShadoColors.Warning)
                        }
                    }
                }

                // Decode: File card + Actions
                if (!isEncoded) {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
                        shape = RoundedCornerShape(ShadoDimens.cornerMd),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Gold.copy(alpha = 0.3f))
                    ) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.PictureAsPdf, null, Modifier.size(48.dp), tint = ShadoColors.Gold)
                            Spacer(Modifier.height(10.dp))
                            Text("document.pdf", style = MaterialTheme.typography.titleMedium, color = ShadoColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Extracted file", style = MaterialTheme.typography.bodySmall, color = ShadoColors.TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text("2.45 MB", style = MaterialTheme.typography.bodySmall, color = ShadoColors.Gold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(ShadoDimens.btnHeight),
                            colors = ButtonDefaults.buttonColors(containerColor = ShadoColors.Gold, contentColor = Color(0xFF1A0A00)),
                            shape = RoundedCornerShape(ShadoDimens.cornerMd)
                        ) { Icon(Icons.Outlined.SaveAlt, null, Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text("Save", fontWeight = FontWeight.SemiBold) }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(ShadoDimens.btnHeight),
                                shape = RoundedCornerShape(ShadoDimens.cornerMd),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Gold)
                            ) { Icon(Icons.Default.Share, null, tint = ShadoColors.Gold); Spacer(Modifier.width(8.dp)); Text("Share", color = ShadoColors.Gold) }

                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(ShadoDimens.btnHeight),
                                shape = RoundedCornerShape(ShadoDimens.cornerMd),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Border)
                            ) { Icon(Icons.Outlined.OpenInNew, null, tint = ShadoColors.TextSecondary); Spacer(Modifier.width(8.dp)); Text("Open", color = ShadoColors.TextSecondary) }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                OutlinedButton(
                    onClick = onNavigateHome,
                    modifier = Modifier.fillMaxWidth().height(ShadoDimens.btnHeight),
                    shape = RoundedCornerShape(ShadoDimens.cornerMd),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.Border)
                ) {
                    Icon(Icons.Default.Home, null, tint = ShadoColors.TextSecondary)
                    Spacer(Modifier.width(8.dp))
                    Text("Go Home", color = ShadoColors.TextSecondary)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
