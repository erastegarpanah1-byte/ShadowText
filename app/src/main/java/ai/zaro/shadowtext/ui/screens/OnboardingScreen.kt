package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            StepIndicator(currentStep = step, totalSteps = 3)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            ConstrainedColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    0 -> LanguageStep(onSelect = { code ->
                        langCode = code
                        onLanguageSelected(code)
                    })
                    1 -> ThemeStep(
                        isDark = isDark,
                        onSelect = { dark -> isDark = dark; step = 2 },
                        onBack = { step = 0 }
                    )
                    2 -> WelcomeStep(onGetStarted = { onComplete(langCode, isDark) })
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    val c = MaterialTheme.colorScheme
    val activeColor = c.primary
    val inactiveColor = c.outline.copy(alpha = 0.5f)
    val dotSize = 36.dp
    val barHeight = 2.dp

    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until totalSteps) {
            val stepNumber = i + 1
            val isActive = i == currentStep
            val isCompleted = i < currentStep

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> activeColor
                            isCompleted -> activeColor.copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }
                    )
                    .then(
                        if (!isActive && !isCompleted) {
                            Modifier.border(1.5.dp, inactiveColor, CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = when {
                        isActive -> c.onPrimary
                        isCompleted -> activeColor
                        else -> inactiveColor
                    }
                )
            }

            if (i < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .width(32.dp)
                        .background(if (i < currentStep) activeColor.copy(alpha = 0.4f) else inactiveColor.copy(alpha = 0.3f))
                )
            }
        }
    }
}

@Composable
private fun LanguageStep(onSelect: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    Icon(Icons.Outlined.Language, null, Modifier.size(56.dp), tint = c.primary)
    Spacer(Modifier.height(24.dp))
    Text(stringResource(R.string.onboarding_language_title), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.onboarding_language_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(40.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        LangCard("فارسی", Modifier.weight(1f)) { onSelect("fa") }
        LangCard("ENGLISH", Modifier.weight(1f)) { onSelect("en") }
    }
}

@Composable
private fun LangCard(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Card(modifier.aspectRatio(1.5f).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), border = androidx.compose.foundation.BorderStroke(1.dp, c.outline.copy(alpha = 0.2f))) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onSurface, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ThemeStep(isDark: Boolean, onSelect: (Boolean) -> Unit, onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Icon(Icons.Outlined.LightMode, null, Modifier.size(56.dp), tint = c.primary)
    Spacer(Modifier.height(24.dp))
    Text(stringResource(R.string.onboarding_theme_title), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.onboarding_theme_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(40.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        ThemeCard(stringResource(R.string.onboarding_theme_light), !isDark, true, Modifier.weight(1f)) { onSelect(false) }
        ThemeCard(stringResource(R.string.onboarding_theme_dark), isDark, false, Modifier.weight(1f)) { onSelect(true) }
    }
    Spacer(Modifier.height(32.dp))
    TextButton(onClick = onBack) { Text(stringResource(R.string.back), color = c.onSurfaceVariant) }
}

@Composable
private fun ThemeCard(label: String, isSelected: Boolean, isLightPreview: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val bg = if (isLightPreview) Color(0xFFFAF7F2) else Color(0xFF070E17)
    val accent = if (isLightPreview) Color(0xFFB8860B) else Color(0xFFD4A574)
    Card(modifier.aspectRatio(1f / 1.3f).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bg), border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) c.primary else Color.Transparent)) {
        Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.fillMaxWidth(0.85f).weight(1f).clip(RoundedCornerShape(10.dp)).background(if (isLightPreview) Color(0xFFF5F0E8) else Color(0xFF0A1628))) {
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

@Composable
private fun WelcomeStep(onGetStarted: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Image(painter = painterResource(id = R.drawable.logo_shadowtext), contentDescription = "Logo", modifier = Modifier.size(120.dp).clip(RoundedCornerShape(24.dp)), contentScale = ContentScale.Fit)
    Spacer(Modifier.height(28.dp))
    Text(stringResource(R.string.onboarding_welcome_title), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = c.onBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    Text(stringResource(R.string.onboarding_welcome_subtitle), style = MaterialTheme.typography.bodyLarge, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 24.sp, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(44.dp))
    Button(onClick = onGetStarted, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.primary)) {
        Text(stringResource(R.string.onboarding_get_started), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = c.onPrimary)
    }
}
