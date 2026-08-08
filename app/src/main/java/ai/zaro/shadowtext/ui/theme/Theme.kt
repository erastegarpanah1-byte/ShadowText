package ai.zaro.shadowtext.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Gold, onPrimary = Color(0xFF0B1E33),
    primaryContainer = Color(0xFF3D2E1E), onPrimaryContainer = GoldLight,
    secondary = TealAccent, onSecondary = Color(0xFF00382E),
    secondaryContainer = Color(0xFF005143), onSecondaryContainer = Color(0xFFA7FDE8),
    tertiary = Color(0xFF7CB8E8), onTertiary = Color(0xFF0B1E33),
    background = Navy950, onBackground = TextWhite,
    surface = Navy900, onSurface = TextWhite,
    surfaceVariant = NavyCard, onSurfaceVariant = TextDimWhite,
    outline = NavyBorder, outlineVariant = NavyBorderSubtle,
    error = ErrorRed, onError = Color(0xFF561E16),
    errorContainer = ErrorBg, onErrorContainer = Color(0xFFFFDAD4),
)

private val LightColorScheme = lightColorScheme(
    primary = GoldLightMode, onPrimary = Color.White,
    primaryContainer = GoldBg, onPrimaryContainer = GoldText,
    secondary = TealLight, onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB), onSecondaryContainer = Color(0xFF00201B),
    tertiary = Color(0xFF5D6D46), onTertiary = Color.White,
    background = BeigeBg, onBackground = TextDark,
    surface = Color.White, onSurface = TextDark,
    surfaceVariant = SurfaceLight, onSurfaceVariant = TextGray,
    outline = BorderLight, outlineVariant = BorderLightSubtle,
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD4), onErrorContainer = Color(0xFF410002),
)

@Composable
fun ShadowTextTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
