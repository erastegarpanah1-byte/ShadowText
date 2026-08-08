package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.navigation.ShadowTextNavHost
import ai.zaro.shadowtext.ui.theme.ShadowTextTheme
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(true) }
            ShadowTextTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ShadowTextNavHost(isDarkMode = isDarkMode, onToggleDarkMode = { isDarkMode = it })
                }
            }
        }
    }
}
