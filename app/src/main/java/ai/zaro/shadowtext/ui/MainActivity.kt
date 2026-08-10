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

class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "shadowtext_prefs"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ONBOARDING_STEP = "onboarding_step"
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
        val savedDarkMode = prefs.getBoolean(KEY_DARK_MODE, true)
        val savedStep = savedInstanceState?.getInt(KEY_ONBOARDING_STEP, 0) ?: 0

        setContent {
            var showOnboarding by remember { mutableStateOf(!onboardingDone) }
            var isDarkMode by remember { mutableStateOf(savedDarkMode) }
            val context = LocalContext.current

            val effectiveTheme = if (showOnboarding) true else isDarkMode

            ShadowTextTheme(darkTheme = effectiveTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showOnboarding) {
                        OnboardingScreen(
                            initialStep = savedStep,
                            onLanguageSelected = { languageCode ->
                                LocaleHelper.setLocale(context, languageCode)
                                prefs.edit()
                                    .putInt(KEY_ONBOARDING_STEP, 1)
                                    .apply()
                                recreate()
                            },
                            onComplete = { languageCode, dark ->
                                prefs.edit()
                                    .putBoolean(KEY_ONBOARDING_DONE, true)
                                    .putBoolean(KEY_DARK_MODE, dark)
                                    .putInt(KEY_ONBOARDING_STEP, 0)
                                    .apply()
                                showOnboarding = false
                                isDarkMode = dark
                                recreate()
                            }
                        )
                    } else {
                        ShadowTextNavHost(
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { dark ->
                                isDarkMode = dark
                                prefs.edit().putBoolean(KEY_DARK_MODE, dark).apply()
                            },
                            languageCode = LocaleHelper.getLanguage(context),
                            onChangeLanguage = { code ->
                                LocaleHelper.setLocale(context, code)
                                recreate()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        outState.putInt(KEY_ONBOARDING_STEP, prefs.getInt(KEY_ONBOARDING_STEP, 0))
    }
}
