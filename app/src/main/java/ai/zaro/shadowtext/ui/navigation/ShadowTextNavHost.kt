package ai.zaro.shadowtext.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ai.zaro.shadowtext.ui.screens.*

object Routes {
    const val HOME = "home"
    const val CHOOSE_INPUT = "choose_input/{mode}"
    const val ENCODE_INPUT = "encode_input"
    const val ENCODE_OPTIONS = "encode_options/{inputText}/{secretText}"
    const val ENCODE_RESULT = "encode_result/{stegoText}"
    const val DECODE_INPUT = "decode_input"
    const val DECODE_OPTIONS = "decode_options/{inputText}"
    const val DECODE_RESULT = "decode_result/{decodedText}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun ShadowTextNavHost(
    isDarkMode: Boolean = true,
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showSplash = remember { mutableStateOf(true) }

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Routes.HISTORY, "History", Icons.Filled.DateRange, Icons.Outlined.DateRange),
        BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.HISTORY, Routes.SETTINGS)

    if (showSplash.value) {
        SplashScreen(onFinished = { showSplash.value = false })
        return
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val sel = currentRoute == item.route
                        NavigationBarItem(
                            selected = sel,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    if (sel) item.selectedIcon else item.unselectedIcon,
                                    item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onEncodeClick = { navController.navigate("choose_input/encode") },
                    onDecodeClick = { navController.navigate("choose_input/decode") }
                )
            }
            composable(
                route = Routes.CHOOSE_INPUT,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }
            ) { be ->
                val mode = be.arguments?.getString("mode") ?: "encode"
                ChooseInputScreen(
                    mode = mode,
                    onBack = { navController.popBackStack() },
                    onTextSelected = {
                        if (mode == "encode") navController.navigate(Routes.ENCODE_INPUT)
                        else navController.navigate(Routes.DECODE_INPUT)
                    },
                    onFileSelected = {
                        if (mode == "encode") navController.navigate(Routes.ENCODE_INPUT)
                        else navController.navigate(Routes.DECODE_INPUT)
                    }
                )
            }
            composable(Routes.ENCODE_INPUT) {
                EncodeInputScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { inputText, secretText ->
                        navController.navigate("encode_options/$inputText/$secretText")
                    }
                )
            }
            composable(Routes.ENCODE_OPTIONS) { be ->
                val inputText = be.arguments?.getString("inputText") ?: ""
                val secretText = be.arguments?.getString("secretText") ?: ""
                EncodeOptionsScreen(
                    inputText = inputText,
                    secretText = secretText,
                    onBack = { navController.popBackStack() },
                    onEncode = { _, _, _, _, _, _ ->
                        val result = "[ENCODED] $secretText"
                        navController.navigate("encode_result/$result") {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }
            composable(Routes.ENCODE_RESULT) { be ->
                val stegoText = be.arguments?.getString("stegoText") ?: ""
                EncodeResultScreen(
                    stegoText = stegoText,
                    onBack = { navController.popBackStack(Routes.HOME, false) },
                    onNew = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.DECODE_INPUT) {
                DecodeInputScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { inputText ->
                        navController.navigate("decode_options/$inputText")
                    }
                )
            }
            composable(Routes.DECODE_OPTIONS) { be ->
                val inputText = be.arguments?.getString("inputText") ?: ""
                DecodeOptionsScreen(
                    inputText = inputText,
                    onBack = { navController.popBackStack() },
                    onDecode = { _, _, _ ->
                        val result = "[DECODED] Your hidden text here"
                        navController.navigate("decode_result/$result") {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }
            composable(Routes.DECODE_RESULT) { be ->
                val decodedText = be.arguments?.getString("decodedText") ?: ""
                DecodeResultScreen(
                    decodedText = decodedText,
                    onBack = { navController.popBackStack(Routes.HOME, false) },
                    onNew = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SETTINGS) {
                SettingsScreen(isDarkMode = isDarkMode, onToggleDarkMode = onToggleDarkMode)
            }
        }
    }
}
