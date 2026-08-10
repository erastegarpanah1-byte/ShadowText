package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 3-step onboarding matching the bilingual mockup:
 *   Step 0 – Language (with globe icon, "1/3")
 *   Step 1 – Theme (with sun/moon icons, "2/3")
 *   Step 2 – Welcome (with shield icon, "3/3")
 */
@Composable
fun OnboardingScreen(
    initialStep: Int = 0,
    onLanguageSelected: (languageCode: String) -> Unit = {},
    onComplete: (languageCode: String, isDarkMode: Boolean) -> Unit = { _, _ -> }
) {
    var step by remember { mutableStateOf(initialStep) }
    var langCode by remember { mutableStateOf("en") }
    var isDark by remember { mutableStateOf(true) }

    val c = MaterialTheme.colorScheme

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).padding(top = 12.dp), horizontalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(R.string.onboarding_step, if (step == 2) 3 else step + 1, 3),
            style = MaterialTheme.typography.labelLarge,
            color = c.onBackground.copy(alpha = 0.5f)
        )
    }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
        ConstrainedColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            when (step) {
                0 -> LanguageStep(onSelect = { code -> langCode = code; onLanguageSelected(code) })
                1 -> ThemeStep(isDark = isDark, onSelect = { dark -> isDark = dark; step = 2 }, onBack = { step = 0 })
                2 -> WelcomeStep(onGetStarted = { onComplete(langCode, isDark) })
            }
        }
    }
}

// ── Step 0: Language ──────────────────────────────────────────────

@Composable
private fun LanguageStep(onSelect: (String) -> Unit) {
    val c = MaterialTheme.colorScheme

    Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(56.dp), tint = c.primary)
    Spacer(Modifier.height(24.dp))

    Text(stringResource(R.string.onboarding_language_title), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center)
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.onboarding_language_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 20.sp)
    Spacer(Modifier.height(40.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        LangCard("فارسی", isSelected = false, modifier = Modifier.weight(1f), onClick = { onSelect("fa") })
        LangCard("ENGLISH", isSelected = false, modifier = Modifier.weight(1f), onClick = { onSelect("en") })
    }
}

@Composable
private fun LangCard(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Card(modifier = modifier.aspectRatio(1.5f).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) c.primary else c.outline.copy(alpha = 0.2f))) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onSurface, textAlign = TextAlign.Center)
        }
    }
}

// ── Step 1: Theme ─────────────────────────────────────────────────

@Composable
private fun ThemeStep(isDark: Boolean, onSelect: (Boolean) -> Unit, onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme

    Icon(Icons.Outlined.LightMode, contentDescription = null, modifier = Modifier.size(56.dp), tint = c.primary)
    Spacer(Modifier.height(24.dp))

    Text(stringResource(R.string.onboarding_theme_title), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center)
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.onboarding_theme_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 20.sp)
    Spacer(Modifier.height(40.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        ThemeCard(stringResource(R.string.onboarding_theme_light), isSelected = !isDark, isLightPreview = true, modifier = Modifier.weight(1f), onClick = { onSelect(false) })
        ThemeCard(stringResource(R.string.onboarding_theme_dark), isSelected = isDark, isLightPreview = false, modifier = Modifier.weight(1f), onClick = { onSelect(true) })
    }

    Spacer(Modifier.height(32.dp))
    TextButton(onClick = onBack) { Text(stringResource(R.string.back), color = c.onSurfaceVariant) }
}

@Composable
private fun ThemeCard(label: String, isSelected: Boolean, isLightPreview: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val bg = if (isLightPreview) Color(0xFFFAF7F2) else Color(0xFF070E17)
    val accent = if (isLightPreview) Color(0xFFB8860B) else Color(0xFFD4A574)

    Card(modifier = modifier.aspectRatio(1f / 1.3f).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bg), border = androidx.compose.foundation.BorderStroke(1.5f.dp, if (isSelected) c.primary else Color.Transparent)) {
        Column(modifier = Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxWidth(0.85f).weight(1f).clip(RoundedCornerShape(10.dp)).background(if (isLightPreview) Color(0xFFF5F0E8) else Color(0xFF0A1628))) {
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    val lc = if (isLightPreview) Color(0xFFE8E2D5) else Color(0xFF1E3050)
                    Box(Modifier.fillMaxWidth(0.7f).height(5.dp).clip(RoundedCornerShape(2.dp)).background(lc))
                    Box(Modifier.fillMaxWidth(0.9f).height(5.dp).clip(RoundedCornerShape(2.dp)).background(lc))
                    Box(Modifier.fillMaxWidth(0.5f).height(5.dp).clip(RoundedCornerShape(2.dp)).background(lc))
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.fillMaxWidth(0.35f).height(16.dp).clip(RoundedCornerShape(8.dp)).background(accent))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium), color = if (isLightPreview) Color(0xFF1A1C1E) else Color(0xFFE2E2E8), textAlign = TextAlign.Center)
        }
    }
}

// ── Step 2: Welcome ───────────────────────────────────────────────

@Composable
private fun WelcomeStep(onGetStarted: () -> Unit) {
    val c = MaterialTheme.colorScheme

    Icon(Icons.Outlined.Security, contentDescription = null, modifier = Modifier.size(64.dp), tint = c.primary)
    Spacer(Modifier.height(28.dp))

    Text(stringResource(R.string.onboarding_welcome_title), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center)
    Spacer(Modifier.height(10.dp))
    Text(stringResource(R.string.onboarding_welcome_subtitle), style = MaterialTheme.typography.bodyLarge, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 24.sp)
    Spacer(Modifier.height(44.dp))

    Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary)) {
        Text(stringResource(R.string.onboarding_get_started), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onPrimary)
    }
}
