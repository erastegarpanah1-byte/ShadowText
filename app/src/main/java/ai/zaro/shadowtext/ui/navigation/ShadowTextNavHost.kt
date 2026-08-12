package ai.zaro.shadowtext.ui.navigation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.client.compose.composable
import androidx.client.compose.currentBackStackEntryAsState
import androidx.client.compose.rememberNavController
import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Routes {
    const val HOME = "home"
    const val ENCODE_INPUT = "encode_input"
    const val ENCODE_RESULT = "encode_result"
    const val DECODE_INPUT = "decode_input"
    const val DECODE_RESULT = "decode_result"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

data class BottomNavItem(val route: String, val labelRes: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

@Composable
fun ShadowTextNavHost(isDarkMode: Boolean = true, onToggleDarkMode: (Boolean) -> Unit = {}, languageCode: String = "en", onChangeLanguage: (String) -> Unit = {}) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showSplash = remember { mutableStateOf(true) }
    val c = MaterialTheme.colorScheme

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Routes.HISTORY, R.string.nav_history, Icons.Filled.DateRange, Icons.Outlined.DateRange),
        BottomNavItem(Routes.SETTINGS, R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.HISTORY, Routes.SETTINGS)

    if (showSplash.value) { SplashScreen(onFinished = { showSplash.value = false }); return }

    Scaffold(bottomBar = {
        if (showBottomBar) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(28.dp), color = c.surfaceVariant, shadowElevation = 8.dp) {
                    NavigationBar(containerColor = c.surfaceVariant, tonalElevation = 0.dp, modifier = Modifier.clip(RoundedCornerShape(28.dp))) {
                        bottomNavItems.forEach { item ->
                            val sel = currentRoute == item.route
                            NavigationBarItem(selected = sel, onClick = { if (currentRoute != item.route) { navController.navigate(item.route) { popUpTo(Routes.HOME) { saveState = true }; launchSingleTop = true; restoreState = true } } }, icon = { Icon(if (sel) item.selectedIcon else item.unselectedIcon, stringResource(item.labelRes)) }, label = { Text(stringResource(item.labelRes)) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = c.primary, selectedTextColor = c.primary, unselectedIconColor = c.onSurfaceVariant, unselectedTextColor = c.onSurfaceVariant, indicatorColor = c.primary.copy(alpha = 0.12f)))
                        }
                    }
                }
            }
        }
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = Routes.HOME, modifier = Modifier.padding(innerPadding)) {
            composable(Routes.HOME) { HomeScreen(onEncodeClick = { navController.navigate(Routes.ENCODE_INPUT) }, onDecodeClick = { navController.navigate(Routes.DECODE_INPUT) }) }
            composable(Routes.ENCODE_INPUT) {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                EncodeInputScreen(onBack = { navController.popBackStack() }, onEncode = { inputText, secretText ->
                    scope.launch {
                        try {
                            val encoder = ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder()
                            val pkt = ai.zaro.shadowtext.core.format.Packet(ai.zaro.shadowtext.core.format.PacketFormat.CURRENT_VERSION, ai.zaro.shadowtext.core.format.PacketFormat.Flags.NONE, ai.zaro.shadowtext.core.format.PacketFormat.PayloadType.PLAIN_TEXT, secretText.toByteArray(Charsets.UTF_8), mapOf("filename" to "", "mimeType" to "text/plain", "encodedAt" to System.currentTimeMillis().toString()))
                            val serialized = ai.zaro.shadowtext.core.format.PacketSerializer.serialize(pkt)
                            val inv = encoder.encode(serialized)
                            val stego = encoder.embed(inputText, inv)
                            EncodeResultHolder.result = stego
                            EncodeResultHolder.error = null
                            if (isHistoryEnabled(ctx)) HistoryStore.add(ctx, HistoryEntry(type = "encode", inputPreview = inputText.take(80), outputPreview = stego.take(80)))
                            navController.navigate(Routes.ENCODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } }
                        } catch (e: Exception) {
                            Log.e("ShadowText", "Encode failed", e)
                            EncodeResultHolder.result = null
                            EncodeResultHolder.error = e.message
                            if (isHistoryEnabled(ctx)) HistoryStore.add(ctx, HistoryEntry(type = "encode", inputPreview = inputText.take(80), outputPreview = "", status = "failed"))
                            navController.navigate(Routes.ENCODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } }
                        }
                    }
                })
            }
            composable(Routes.ENCODE_RESULT) {
                val stego = remember { EncodeResultHolder.result }
                val error = remember { EncodeResultHolder.error }
                LaunchedEffect(Unit) { EncodeResultHolder.result = null; EncodeResultHolder.error = null }
                EncodeResultScreen(stegoText = stego, errorText = error, onBack = { navController.popBackStack(Routes.HOME, false) }, onNew = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } })
            }
            composable(Routes.DECODE_INPUT) {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                DecodeInputScreen(onBack = { navController.popBackStack() }, onDecode = { inputText ->
                    scope.launch {
                        try {
                            val encoder = ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder()
                            val decoder = ai.zaro.shadowtext.core.engine.StegoDecoder(listOf(encoder))
                            val result = withContext(Dispatchers.Default) { decoder.decode(inputText) }
                            val decodedStr = String(result.payload, Charsets.UTF_8)
                            DecodeResultHolder.result = decodedStr
                            DecodeResultHolder.error = null
                            if (isHistoryEnabled(ctx)) HistoryStore.add(ctx, HistoryEntry(type = "decode", inputPreview = inputText.take(80), outputPreview = decodedStr.take(80)))
                            navController.navigate(Routes.DECODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } }
                        } catch (e: Exception) {
                            Log.e("ShadowText", "Decode failed", e)
                            DecodeResultHolder.result = null
                            DecodeResultHolder.error = e.message
                            if (isHistoryEnabled(ctx)) HistoryStore.add(ctx, HistoryEntry(type = "decode", inputPreview = inputText.take(80), outputPreview = "", status = "failed"))
                            navController.navigate(Routes.DECODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } }
                        }
                    }
                })
            }
            composable(Routes.DECODE_RESULT) {
                val text = remember { DecodeResultHolder.result }
                val error = remember { DecodeResultHolder.error }
                LaunchedEffect(Unit) { DecodeResultHolder.result = null; DecodeResultHolder.error = null }
                DecodeResultScreen(decodedText = text, errorText = error, onBack = { navController.popBackStack(Routes.HOME, false) }, onNew = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } })
            }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SETTINGS) { SettingsScreen(isDarkMode = isDarkMode, onToggleDarkMode = onToggleDarkMode, languageCode = languageCode, onChangeLanguage = onChangeLanguage, onNavigateToAbout = { navController.navigate(Routes.ABOUT) }) }
            composable(Routes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

internal fun isHistoryEnabled(context: Context): Boolean = context.getSharedPreferences("shadowtext_prefs", Context.MODE_PRIVATE).getBoolean("history_enabled", true)
internal object EncodeResultHolder { var result: String? = null; var error: String? = null }
internal object DecodeResultHolder { var result: String? = null; var error: String? = null }
