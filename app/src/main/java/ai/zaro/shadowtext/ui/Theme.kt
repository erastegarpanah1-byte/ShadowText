package ai.zaro.shadowtext.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NavyPetrol = darkColorScheme(
    primary = Color(0xFFD4A574),
    onPrimary = Color(0xFF0B1E33),
    primaryContainer = Color(0xFF3D2E1E),
    onPrimaryContainer = Color(0xFFFFE5C8),
    secondary = Color(0xFF2ED4B4),
    onSecondary = Color(0xFF00382E),
    secondaryContainer = Color(0xFF005143),
    onSecondaryContainer = Color(0xFFA7FDE8),
    tertiary = Color(0xFF7CB8E8),
    onTertiary = Color(0xFF0B1E33),
    tertiaryContainer = Color(0xFF1A3550),
    onTertiaryContainer = Color(0xFFD1E4FF),
    background = Color(0xFF070E17),
    onBackground = Color(0xFFE2E2E8),
    surface = Color(0xFF0D1625),
    onSurface = Color(0xFFE2E2E8),
    surfaceVariant = Color(0xFF162033),
    onSurfaceVariant = Color(0xFFC1C6CF),
    outline = Color(0xFF3D5068),
    outlineVariant = Color(0xFF263450),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF561E16),
    errorContainer = Color(0xFF723428),
    onErrorContainer = Color(0xFFFFDAD4),
    inverseSurface = Color(0xFFE2E2E8),
    inverseOnSurface = Color(0xFF0D1625),
    inversePrimary = Color(0xFF6B5438),
)

@Composable
fun ShadowTextTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NavyPetrol,
        content = content,
    )
}
