package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 3-step onboarding shown on first launch:
 *   Step 1 – Language selection (فارسی | ENGLISH)
 *   Step 2 – Theme selection (Dark | Light) — in the chosen language
 *   Step 3 – Welcome screen — with chosen language & theme
 */
@Composable
fun OnboardingScreen(
    onComplete: (languageCode: String, isDarkMode: Boolean) -> Unit
) {
    // ---- local state ----
    var step by remember { mutableStateOf(0) }
    var langCode by remember { mutableStateOf("en") }
    var isDark by remember { mutableStateOf(true) }

    // Wrap each step in the current theme + language so the user sees it live
    val c = MaterialTheme.colorScheme
    val totalSteps = 3

    Scaffold(
        containerColor = c.background,
        topBar = {
            // Step indicator bar
            if (step < totalSteps - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (i == step) 28.dp else 10.dp)
                                .clip(if (i == step) RoundedCornerShape(14.dp) else CircleShape)
                                .background(
                                    if (i <= step) c.primary
                                    else c.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }
            }
        }
    ) { padding ->
        ConstrainedColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                0 -> LanguageStep(
                    onSelect = { code ->
                        langCode = code
                        step = 1
                    }
                )
                1 -> ThemeStep(
                    langCode = langCode,
                    isDark = isDark,
                    onSelect = { dark ->
                        isDark = dark
                        step = 2
                    },
                    onBack = { step = 0 }
                )
                2 -> WelcomeStep(
                    onGetStarted = { onComplete(langCode, isDark) },
                    onBack = { step = 1 }
                )
            }
        }
    }
}

// ── Step 1: Language ──────────────────────────────────────────────

@Composable
private fun LanguageStep(onSelect: (String) -> Unit) {
    val c = MaterialTheme.colorScheme

    Text(
        text = stringResource(R.string.onboarding_language_title),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = c.onBackground,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.onboarding_language_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(40.dp))

    // Two large cards
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        LanguageCard(
            label = "فارسی",
            onClick = { onSelect("fa") },
            modifier = Modifier.weight(1f)
        )
        LanguageCard(
            label = "ENGLISH",
            onClick = { onSelect("en") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LanguageCard(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    Card(
        modifier = modifier
            .aspectRatio(1f / 1.3f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = c.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, c.outline.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = c.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Step 2: Theme ─────────────────────────────────────────────────

@Composable
private fun ThemeStep(
    langCode: String,
    isDark: Boolean,
    onSelect: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val c = MaterialTheme.colorScheme

    Text(
        text = stringResource(R.string.onboarding_theme_title),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = c.onBackground,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.onboarding_theme_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(40.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        ThemeCard(
            label = stringResource(R.string.onboarding_theme_light),
            isSelected = !isDark,
            isLightPreview = true,
            onClick = { onSelect(false) },
            modifier = Modifier.weight(1f)
        )
        ThemeCard(
            label = stringResource(R.string.onboarding_theme_dark),
            isSelected = isDark,
            isLightPreview = false,
            onClick = { onSelect(true) },
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(32.dp))
    OutlinedButton(onClick = onBack) {
        Text(stringResource(R.string.back))
    }
}

@Composable
private fun ThemeCard(
    label: String,
    isSelected: Boolean,
    isLightPreview: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = MaterialTheme.colorScheme
    val bg = if (isLightPreview)
        androidx.compose.ui.graphics.Color(0xFFFAF7F2)
    else
        androidx.compose.ui.graphics.Color(0xFF070E17)
    val accent = if (isLightPreview)
        androidx.compose.ui.graphics.Color(0xFFB8860B)
    else
        androidx.compose.ui.graphics.Color(0xFFD4A574)

    Card(
        modifier = modifier
            .aspectRatio(1f / 1.3f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, c.primary)
        else
            androidx.compose.foundation.BorderStroke(1.dp, c.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mini preview
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isLightPreview)
                            androidx.compose.ui.graphics.Color(0xFFF5F0E8)
                        else
                            androidx.compose.ui.graphics.Color(0xFF0A1628)
                    )
            ) {
                // Fake text lines
                Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val lineColor = if (isLightPreview)
                        androidx.compose.ui.graphics.Color(0xFFE8E2D5)
                    else
                        androidx.compose.ui.graphics.Color(0xFF1E3050)
                    Box(Modifier.fillMaxWidth(0.7f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(lineColor))
                    Box(Modifier.fillMaxWidth(0.9f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(lineColor))
                    Box(Modifier.fillMaxWidth(0.5f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(lineColor))
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.fillMaxWidth(0.3f).height(20.dp).clip(RoundedCornerShape(10.dp)).background(accent))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                color = if (isLightPreview) androidx.compose.ui.graphics.Color(0xFF1A1C1E) else androidx.compose.ui.graphics.Color(0xFFE2E2E8),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Step 3: Welcome ───────────────────────────────────────────────

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    onBack: () -> Unit
) {
    val c = MaterialTheme.colorScheme

    Spacer(Modifier.height(24.dp))

    // App icon placeholder
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(c.primary, c.secondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "S",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = c.onPrimary
            )
        )
    }

    Spacer(Modifier.height(32.dp))

    Text(
        text = stringResource(R.string.onboarding_welcome_title),
        style = MaterialTheme.typography.headlineSmall,
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Text(
        text = "ShadowText",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        color = c.primary,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.onboarding_welcome_subtitle),
        style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.onboarding_welcome_desc),
        style = MaterialTheme.typography.bodyMedium,
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
    )

    Spacer(Modifier.height(48.dp))

    Button(
        onClick = onGetStarted,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = c.primary)
    ) {
        Text(
            text = stringResource(R.string.onboarding_get_started),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = c.onPrimary
        )
    }

    Spacer(Modifier.height(16.dp))
    OutlinedButton(onClick = onBack) {
        Text(stringResource(R.string.back))
    }
}
