package ai.zaro.shadowtext.ui.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val prefs = context.getSharedPreferences("shadowtext_prefs", Context.MODE_PRIVATE)
    var isDarkMode by mutableStateOf(prefs.getBoolean("dark_mode", true)); private set
    var localeCode by mutableStateOf(prefs.getString("locale", "fa") ?: "fa"); private set
    fun setDarkMode(dark: Boolean) { isDarkMode = dark; prefs.edit().putBoolean("dark_mode", dark).apply() }
    fun setLocale(code: String) { localeCode = code; prefs.edit().putString("locale", code).apply() }
}
