package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val VazirmatnFamily = FontFamily(Font(R.font.vazirmatn_regular, FontWeight.Normal), Font(R.font.vazirmatn_bold, FontWeight.Bold))

val ShadowTypography = Typography(
    displayLarge = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    displayMedium = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    displaySmall = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineLarge = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineMedium = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    headlineSmall = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = VazirmatnFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp, lineHeight = 14.sp),
)

val NavyPetrol = darkColorScheme(primary=Color(0xFFD4A574),onPrimary=Color(0xFF0B1E33),primaryContainer=Color(0xFF3D2E1E),onPrimaryContainer=Color(0xFFFFE5C8),secondary=Color(0xFF2ED4B4),onSecondary=Color(0xFF00382E),secondaryContainer=Color(0xFF005143),onSecondaryContainer=Color(0xFFA7FDE8),tertiary=Color(0xFF7CB8E8),onTertiary=Color(0xFF0B1E33),tertiaryContainer=Color(0xFF1A3550),onTertiaryContainer=Color(0xFFD1E4FF),background=Color(0xFF070E17),onBackground=Color(0xFFE2E2E8),surface=Color(0xFF0D1625),onSurface=Color(0xFFE2E2E8),surfaceVariant=Color(0xFF162033),onSurfaceVariant=Color(0xFFC1C6CF),outline=Color(0xFF3D5068),outlineVariant=Color(0xFF263450),error=Color(0xFFFFB4AB),onError=Color(0xFF561E16),errorContainer=Color(0xFF723428),onErrorContainer=Color(0xFFFFDAD4),inverseSurface=Color(0xFFE2E2E8),inverseOnSurface=Color(0xFF0D1625),inversePrimary=Color(0xFF6B5438))

val LightScheme = lightColorScheme(primary=Color(0xFF7B5E3B),onPrimary=Color.White,primaryContainer=Color(0xFFFFE5C8),onPrimaryContainer=Color(0xFF2A1A05),secondary=Color(0xFF006B5C),onSecondary=Color.White,secondaryContainer=Color(0xFFA7FDE8),onSecondaryContainer=Color(0xFF00201B),tertiary=Color(0xFF1A5F8F),onTertiary=Color.White,tertiaryContainer=Color(0xFFD1E4FF),onTertiaryContainer=Color(0xFF001D33),background=Color(0xFFF9F8F6),onBackground=Color(0xFF1A1C1E),surface=Color(0xFFF9F8F6),onSurface=Color(0xFF1A1C1E),surfaceVariant=Color(0xFFE7E2DA),onSurfaceVariant=Color(0xFF4A4540),outline=Color(0xFF7A756F),outlineVariant=Color(0xFFCBC5BD),error=Color(0xFFBA1A1A),onError=Color.White,errorContainer=Color(0xFFFFDAD4),onErrorContainer=Color(0xFF410002),inverseSurface=Color(0xFF2F3033),inverseOnSurface=Color(0xFFF1F0EE),inversePrimary=Color(0xFFD4A574))

@Composable
fun ShadowTextTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isDark) NavyPetrol else LightScheme, typography = ShadowTypography, content = content)
}
