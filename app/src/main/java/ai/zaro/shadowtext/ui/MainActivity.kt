package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.navigation.ShadowTextNavHost
import ai.zaro.shadowtext.ui.screens.OnboardingScreen
import ai.zaro.shadowtext.ui.theme.ShadowTextTheme
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat

class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "shadowtext_prefs"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }

    override fun attachBaseContext(newBase: Context) {
        val ctx = LocaleHelper.applySavedLocale(newBase)
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

        setContent {
            var showOnboarding by remember { mutableStateOf(!onboardingDone) }
            var isDarkMode by remember { mutableStateOf(true) }
            val context = LocalContext.current
            var langCode by remember { mutableStateOf(LocaleHelper.getLanguage(context)) }

            val theme = if (showOnboarding) true else isDarkMode
            ShadowTextTheme(darkTheme = theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showOnboarding) {
                        OnboardingScreen(
                            onComplete = { language, dark ->
                                // Apply user choices
                                langCode = language
                                isDarkMode = dark
                                LocaleHelper.setLocale(context, language)
                                // Mark onboarding as done
                                prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                                showOnboarding = false
                                // Recreate to apply locale to attachBaseContext
                                recreate()
                            }
                        )
                    } else {
                        ShadowTextNavHost(
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { isDarkMode = it },
                            languageCode = langCode,
                            onChangeLanguage = { code ->
                                langCode = code
                                LocaleHelper.setLocale(context, code)
                            }
                        )
                    }
                }
            }
        }
    }
}
