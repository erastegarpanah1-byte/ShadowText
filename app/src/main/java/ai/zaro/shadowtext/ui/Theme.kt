package ai.zaro.shadowtext.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D2137),
    primaryContainer = Color(0xFF1A3A5C),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003735),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    background = Color(0xFF111315),
    onBackground = Color(0xFFE2E2E6),
    error = Color(0xFFFFB4AB),
    outline = Color(0xFF8E9199),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF00695C),
    onSecondary = Color.White,
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1B1B1F),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1B1B1F),
    error = Color(0xFFBA1A1A),
    outline = Color(0xFF74777F),
)

@Composable
fun ShadowTextTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
