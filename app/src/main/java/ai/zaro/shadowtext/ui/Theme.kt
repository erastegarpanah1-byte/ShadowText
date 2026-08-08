package ai.zaro.shadowtext.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ShadoColors {
    val BgDarker = Color(0xFF050D18)
    val BgDark = Color(0xFF0A1525)
    val BgCard = Color(0xFF0F1D32)
    val BgCardAlt = Color(0xFF132240)
    val BgSurface = Color(0xFF172840)
    val Accent = Color(0xFF00B4D8)
    val AccentSoft = Color(0xFF0096C7)
    val AccentGlow = Color(0xFF48CAE4)
    val Gold = Color(0xFFF4A261)
    val GoldSoft = Color(0xFFE9C46A)
    val TextPrimary = Color(0xFFE8EDF5)
    val TextSecondary = Color(0xFF9EACC2)
    val TextMuted = Color(0xFF5A6A84)
    val TextDisabled = Color(0xFF3A4B64)
    val Border = Color(0xFF1E3355)
    val BorderFocus = Color(0xFF00B4D8)
    val BorderSubtle = Color(0xFF162540)
    val Success = Color(0xFF2EC4B6)
    val SuccessBg = Color(0xFF0A302C)
    val Error = Color(0xFFFF6B6B)
    val ErrorBg = Color(0xFF351515)
    val Warning = Color(0xFFFFD166)
    val WarningBg = Color(0xFF302710)
    val Info = Color(0xFF4EA8DE)
    val InfoBg = Color(0xFF0A1F30)
    val DrawerBg = Color(0xFF070F1D)
    val DrawerItem = Color(0xFF12213A)
    val DrawerHeader = Color(0xFF0B1A30)
}

val ShadoDarkScheme = darkColorScheme(
    primary = ShadoColors.Accent,
    onPrimary = Color(0xFF001F2B),
    primaryContainer = Color(0xFF004D6B),
    onPrimaryContainer = Color(0xFFB3E6FF),
    secondary = ShadoColors.Gold,
    onSecondary = Color(0xFF2B1500),
    secondaryContainer = Color(0xFF4A2E00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = ShadoColors.Success,
    onTertiary = Color(0xFF00201B),
    tertiaryContainer = Color(0xFF005143),
    onTertiaryContainer = Color(0xFFA7FDE8),
    error = ShadoColors.Error,
    onError = Color(0xFF3D0000),
    errorContainer = Color(0xFF660000),
    onErrorContainer = Color(0xFFFFDAD4),
    background = ShadoColors.BgDark,
    onBackground = ShadoColors.TextPrimary,
    surface = ShadoColors.BgCard,
    onSurface = ShadoColors.TextPrimary,
    surfaceVariant = ShadoColors.BgSurface,
    onSurfaceVariant = ShadoColors.TextSecondary,
    outline = ShadoColors.Border,
    outlineVariant = ShadoColors.BorderSubtle,
    inverseSurface = ShadoColors.TextPrimary,
    inverseOnSurface = ShadoColors.BgDark,
    inversePrimary = ShadoColors.Accent,
)

val ShadoLightScheme = darkColorScheme(
    primary = Color(0xFF0088AA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCEEFFF),
    onPrimaryContainer = Color(0xFF001F2B),
    secondary = Color(0xFF8A5A30),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = Color(0xFF2B1500),
    tertiary = Color(0xFF006B5C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7FDE8),
    onTertiaryContainer = Color(0xFF00201B),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFF8F9FA),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE8EAED),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0EE),
    inversePrimary = ShadoColors.Accent,
)

val ShadoTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, letterSpacing = 0.sp, fontFamily = FontFamily.SansSerif),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 0.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 0.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.15.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, letterSpacing = 0.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.15.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, letterSpacing = 0.5.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, letterSpacing = 0.25.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, letterSpacing = 0.4.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 1.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 1.5.sp),
)

@Composable
fun ShadowTextTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isDark) ShadoDarkScheme else ShadoLightScheme,
        typography = ShadoTypography,
        content = content,
    )
}
