package ai.zaro.shadowtext.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    private const val PREF_LANGUAGE = "app_language"

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.getSharedPreferences("shadowtext_prefs", Context.MODE_PRIVATE)
            .edit().putString(PREF_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences("shadowtext_prefs", Context.MODE_PRIVATE)
            .getString(PREF_LANGUAGE, "en") ?: "en"
    }

    fun applySavedLocale(context: Context): Context {
        val lang = getLanguage(context)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
