package ai.zaro.shadowtext.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.screens.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Routes {
    const val HOME = "home"
    const val CHOOSE_INPUT = "choose_input/{mode}"
    const val ENCODE_INPUT = "encode_input"
    const val ENCODE_OPTIONS = "encode_options/{inputText}/{secretText}"
    const val ENCODE_RESULT = "encode_result"
    const val DECODE_INPUT = "decode_input"
    const val DECODE_OPTIONS = "decode_options/{inputText}"
    const val DECODE_RESULT = "decode_result"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

data class BottomNavItem(val route: String, val labelRes: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

@Composable
fun ShadowTextNavHost(
    isDarkMode: Boolean = true,
    onToggleDarkMode: (Boolean) -> Unit = {},
    languageCode: String = "en",
    onChangeLanguage: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showSplash = remember { mutableStateOf(true) }
    val c = MaterialTheme.colorScheme

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Routes.HISTORY, R.string.nav_history, Icons.Filled.DateRange, Icons.Outlined.DateRange),
        BottomNavItem(Routes.SETTINGS, R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.HISTORY, Routes.SETTINGS)

    if (showSplash.value) { SplashScreen(onFinished = { showSplash.value = false }); return }

    Scaffold(
        bottomBar = {
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
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Routes.HOME, modifier = Modifier.padding(innerPadding)) {
            composable(Routes.HOME) { HomeScreen(onEncodeClick = { navController.navigate("choose_input/encode") }, onDecodeClick = { navController.navigate("choose_input/decode") }) }
            composable(route = Routes.CHOOSE_INPUT, enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) }, exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }) { be ->
                val mode = be.arguments?.getString("mode") ?: "encode"
                ChooseInputScreen(mode = mode, onBack = { navController.popBackStack() }, onTextSelected = { if (mode == "encode") navController.navigate(Routes.ENCODE_INPUT) else navController.navigate(Routes.DECODE_INPUT) }, onFileSelected = {})
            }
            composable(Routes.ENCODE_INPUT) { EncodeInputScreen(onBack = { navController.popBackStack() }, onNext = { inputText, secretText -> navController.navigate("encode_options/$inputText/$secretText") }) }
            composable(Routes.ENCODE_OPTIONS) { be ->
                val context = LocalContext.current; val inputText = be.arguments?.getString("inputText") ?: ""; val secretText = be.arguments?.getString("secretText") ?: ""
                val scope = rememberCoroutineScope(); var isLoading by remember { mutableStateOf(false) }; var errorMsg by remember { mutableStateOf<String?>(null) }; var resultText by remember { mutableStateOf<String?>(null) }
                if (resultText != null) { LaunchedEffect(resultText) { navController.navigate(Routes.ENCODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } } } }
                EncodeOptionsScreen(inputText = inputText, secretText = secretText, isLoading = isLoading, error = errorMsg, onBack = { navController.popBackStack() }, onEncode = { _, _, _, _, _, _ -> isLoading = true; errorMsg = null; scope.launch { try { val encoder = ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder(); val engine = ai.zaro.shadowtext.core.engine.StegoEncoder(encoder); val pkt = ai.zaro.shadowtext.core.format.Packet(ai.zaro.shadowtext.core.format.PacketFormat.CURRENT_VERSION, ai.zaro.shadowtext.core.format.PacketFormat.Flags.NONE, ai.zaro.shadowtext.core.format.PacketFormat.PayloadType.PLAIN_TEXT, secretText.toByteArray(Charsets.UTF_8), mapOf("filename" to "", "mimeType" to "text/plain", "encodedAt" to System.currentTimeMillis().toString())); val inv = encoder.encode(ai.zaro.shadowtext.core.format.PacketSerializer.serialize(pkt)); val stego = encoder.embed(inputText, inv); EncodeResultHolder.result = stego; HistoryStore.add(context, HistoryEntry(type = "encode", inputPreview = inputText.take(80), outputPreview = stego.take(80))); resultText = stego } catch (e: Exception) { errorMsg = "Encode failed: ${e.message}" } finally { isLoading = false } } })
            }
            composable(Routes.ENCODE_RESULT) { val stego = EncodeResultHolder.result ?: ""; EncodeResultScreen(stegoText = stego, onBack = { EncodeResultHolder.result = null; navController.popBackStack(Routes.HOME, false) }, onNew = { EncodeResultHolder.result = null; navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }) }
            composable(Routes.DECODE_INPUT) { DecodeInputScreen(onBack = { navController.popBackStack() }, onNext = { inputText -> navController.navigate("decode_options/$inputText") }) }
            composable(Routes.DECODE_OPTIONS) { be ->
                val context = LocalContext.current; val inputText = be.arguments?.getString("inputText") ?: ""
                val scope = rememberCoroutineScope(); var isLoading by remember { mutableStateOf(false) }; var errorMsg by remember { mutableStateOf<String?>(null) }; var decodedResult by remember { mutableStateOf<String?>(null) }
                if (decodedResult != null) { LaunchedEffect(decodedResult) { navController.navigate(Routes.DECODE_RESULT) { popUpTo(Routes.HOME) { inclusive = false } } } }
                DecodeOptionsScreen(inputText = inputText, isLoading = isLoading, error = errorMsg, onBack = { navController.popBackStack() }, onDecode = { _, _, _ -> isLoading = true; errorMsg = null; scope.launch { try { val encoder = ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder(); val decoder = ai.zaro.shadowtext.core.engine.StegoDecoder(listOf(encoder)); val result = withContext(Dispatchers.Default) { decoder.decode(inputText) }; val decodedStr = String(result.payload, Charsets.UTF_8); DecodeResultHolder.result = decodedStr; HistoryStore.add(context, HistoryEntry(type = "decode", inputPreview = inputText.take(80), outputPreview = decodedStr.take(80))); decodedResult = decodedStr } catch (e: Exception) { errorMsg = "Decode failed: ${e.message}" } finally { isLoading = false } } })
            }
            composable(Routes.DECODE_RESULT) { val text = DecodeResultHolder.result ?: ""; DecodeResultScreen(decodedText = text, onBack = { DecodeResultHolder.result = null; navController.popBackStack(Routes.HOME, false) }, onNew = { DecodeResultHolder.result = null; navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }) }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SETTINGS) { SettingsScreen(isDarkMode = isDarkMode, onToggleDarkMode = onToggleDarkMode, languageCode = languageCode, onChangeLanguage = onChangeLanguage, onNavigateToAbout = { navController.navigate(Routes.ABOUT) }) }
            composable(Routes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

internal object EncodeResultHolder { var result: String? = null }
internal object DecodeResultHolder { var result: String? = null }
