package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.navigation.ShadowTextNavHost
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
    override fun attachBaseContext(newBase: Context) {
        val ctx = LocaleHelper.applySavedLocale(newBase)
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(true) }
            val context = LocalContext.current
            var langCode by remember { mutableStateOf(LocaleHelper.getLanguage(context)) }

            ShadowTextTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
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