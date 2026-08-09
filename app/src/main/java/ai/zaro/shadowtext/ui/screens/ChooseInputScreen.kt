package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseInputScreen(
    mode: String,
    onBack: () -> Unit,
    onTextSelected: () -> Unit,
    onFileSelected: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    val accent = if (mode == "encode") c.primary else c.secondary
    val title = if (mode == "encode") "Encode" else "Decode"

    Scaffold(
        containerColor = c.background,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold, color = c.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = c.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                "Choose Input Type",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = c.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "What do you want to use?",
                style = MaterialTheme.typography.bodyMedium,
                color = c.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Text card
            SelCard(
                icon = Icons.Outlined.TextFields,
                title = "Text",
                subtitle = "Use text as container",
                accent = accent,
                enabled = true,
                badge = null,
                onClick = onTextSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // File card (disabled)
            SelCard(
                icon = Icons.Outlined.InsertDriveFile,
                title = "File",
                subtitle = "Available in upcoming updates",
                accent = accent,
                enabled = false,
                badge = "Coming Soon",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SelCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    enabled: Boolean,
    badge: String?,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val c = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = c.surfaceVariant,
            disabledContainerColor = c.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp)
    ) {
        Box {
            // Horizontal layout: icon left, text right
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (enabled) accent.copy(alpha = 0.1f)
                            else accent.copy(alpha = 0.05f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon, null, Modifier.size(30.dp),
                        tint = if (enabled) accent else c.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                Spacer(Modifier.width(20.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (enabled) c.onSurface else c.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (enabled) c.onSurfaceVariant else c.onSurfaceVariant.copy(alpha = 0.35f)
                    )
                }
            }

            // Badge
            if (badge != null) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            accent.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = accent
                    )
                }
            }
        }
    }
}
