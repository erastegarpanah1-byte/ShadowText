package ai.zaro.shadowtext.ui.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs = context.getSharedPreferences("shadowtext_prefs", Context.MODE_PRIVATE)

    private val _isDarkMode = mutableStateOf(prefs.getBoolean("dark_mode", true))
    val isDarkMode: Boolean get() = _isDarkMode.value

    private val _localeCode = mutableStateOf(prefs.getString("locale", "fa") ?: "fa")
    val localeCode: String get() = _localeCode.value

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
        prefs.edit().putBoolean("dark_mode", dark).apply()
    }

    fun setLocale(code: String) {
        _localeCode.value = code
        prefs.edit().putString("locale", code).apply()
    }
}
