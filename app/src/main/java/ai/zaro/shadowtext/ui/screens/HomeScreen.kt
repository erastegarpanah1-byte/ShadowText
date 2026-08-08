package ai.zaro.shadowtext.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.zaro.shadowtext.ui.ShadoColors
import ai.zaro.shadowtext.ui.ShadoDimens

@Composable
fun HomeScreen(
    onMenuClick: () -> Unit = {},
    onEncodeClick: () -> Unit,
    onDecodeClick: () -> Unit,
    incomingIntent: Intent?,
    onNavigateToDecode: (String) -> Unit
) {
    LaunchedEffect(incomingIntent) {
        if (incomingIntent?.action == Intent.ACTION_SEND) {
            incomingIntent.getStringExtra(Intent.EXTRA_TEXT)
                ?.takeIf { it.isNotBlank() }
                ?.let(onNavigateToDecode)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ShadoColors.BgDarker, ShadoColors.BgDark, ShadoColors.BgDarker)
                )
            )
    ) {
        // Menu button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            Icon(
                Icons.Filled.Menu,
                "Menu",
                modifier = Modifier.size(26.dp),
                tint = ShadoColors.Accent
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ShadoDimens.paddingScreen),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // ═══ Logo Section ═══
            LogoSection()

            Spacer(Modifier.height(40.dp))

            // ═══ Encode Card ═══
            FeatureCard(
                title = "Encode",
                subtitle = "Hide any file inside ordinary text",
                icon = Icons.Outlined.Lock,
                accentColor = ShadoColors.Accent,
                features = listOf(
                    "Select any file from your device",
                    "Generate invisible stego text",
                    "Copy, share, or send anywhere"
                ),
                onClick = onEncodeClick
            )

            Spacer(Modifier.height(18.dp))

            // ═══ Decode Card ═══
            FeatureCard(
                title = "Decode",
                subtitle = "Extract hidden files from text",
                icon = Icons.Outlined.Search,
                accentColor = ShadoColors.Gold,
                features = listOf(
                    "Paste any text containing hidden data",
                    "Auto-detect encoding scheme",
                    "Extract original file instantly"
                ),
                onClick = onDecodeClick
            )

            Spacer(Modifier.height(32.dp))

            // ═══ Stats Row ═══
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(Icons.Outlined.Security, "Offline", ShadoColors.Success)
                StatItem(Icons.Outlined.VisibilityOff, "Invisible", ShadoColors.Accent)
                StatItem(Icons.Outlined.CloudOff, "No Tracking", ShadoColors.Gold)
            }

            Spacer(Modifier.height(24.dp))

            // ═══ Supported Types ═══
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCardAlt.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(ShadoDimens.cornerMd),
                border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Supports Any File Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = ShadoColors.TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FileTypeTag(Icons.Outlined.Image, "Images")
                        FileTypeTag(Icons.Outlined.Videocam, "Video")
                        FileTypeTag(Icons.Outlined.Audiotrack, "Audio")
                        FileTypeTag(Icons.Outlined.PictureAsPdf, "PDF")
                        FileTypeTag(Icons.Outlined.FolderZip, "ZIP")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "v1.0.0-alpha · Phase 1",
                style = MaterialTheme.typography.labelSmall,
                color = ShadoColors.TextMuted
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ═══ Logo Section ═══
@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo icon with glow ring
        Box(
            modifier = Modifier
                .size(88.dp)
                .shadow(24.dp, CircleShape, ambientColor = ShadoColors.Accent.copy(alpha = 0.3f))
                .background(
                    Brush.linearGradient(
                        listOf(ShadoColors.BgSurface, ShadoColors.BgCard),
                        Offset.Zero,
                        Offset.Infinite
                    ),
                    CircleShape
                )
                .border(1.5.dp, ShadoColors.Accent.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Shield,
                null,
                modifier = Modifier.size(44.dp),
                tint = ShadoColors.Accent
            )
        }

        Spacer(Modifier.height(18.dp))

        // Title
        Text(
            "ShadowText",
            style = MaterialTheme.typography.headlineLarge.copy(
                letterSpacing = 3.sp,
                shadow = Shadow(color = ShadoColors.Accent.copy(alpha = 0.3f), blurRadius = 24f)
            ),
            color = ShadoColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        // Subtitle
        Text(
            "Steganography Engine",
            style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 2.sp),
            color = ShadoColors.Accent.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(8.dp))

        // Divider
        Box(
            Modifier
                .width(80.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, ShadoColors.Accent.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}

// ═══ Feature Card ═══
@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    features: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                16.dp,
                RoundedCornerShape(ShadoDimens.cornerLg),
                ambientColor = accentColor.copy(alpha = 0.12f),
                spotColor = accentColor.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(ShadoDimens.cornerLg))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(ShadoDimens.cornerLg),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(ShadoColors.BgCard, ShadoColors.BgCardAlt),
                        Offset(0f, 0f),
                        Offset(1000f, 1000f)
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha = 0.06f)),
                        Offset.Zero,
                        Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    RoundedCornerShape(ShadoDimens.cornerLg)
                )
        ) {
            Column(Modifier.padding(22.dp)) {
                // Header row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.1f))
                            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, Modifier.size(28.dp), tint = accentColor)
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 2.sp),
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = ShadoColors.TextSecondary
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Feature list
                features.forEach { feature ->
                    Row(
                        Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            null,
                            Modifier.size(16.dp),
                            tint = accentColor.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ShadoColors.TextSecondary.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Open",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        Modifier.size(18.dp),
                        tint = accentColor
                    )
                }
            }
        }
    }
}

// ═══ Stat Item ═══
@Composable
private fun StatItem(icon: ImageVector, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.08f))
                .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(22.dp), tint = color)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
    }
}

// ═══ File Type Tag ═══
@Composable
private fun FileTypeTag(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            null,
            Modifier.size(22.dp),
            tint = ShadoColors.TextMuted
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = ShadoColors.TextMuted
        )
    }
}
