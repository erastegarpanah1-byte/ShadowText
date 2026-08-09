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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.screens.*
import ai.zaro.shadowtext.ui.viewmodel.DecodeViewModel
import ai.zaro.shadowtext.ui.viewmodel.EncodeViewModel

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

    if (showSplash.value) {
        SplashScreen(onFinished = { showSplash.value = false })
        return
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = c.surfaceVariant,
                        shadowElevation = 8.dp
                    ) {
                        NavigationBar(
                            containerColor = c.surfaceVariant,
                            tonalElevation = 0.dp,
                            modifier = Modifier.clip(RoundedCornerShape(28.dp))
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
                                            stringResource(item.labelRes)
                                        )
                                    },
                                    label = { Text(stringResource(item.labelRes)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = c.primary,
                                        selectedTextColor = c.primary,
                                        unselectedIconColor = c.onSurfaceVariant,
                                        unselectedTextColor = c.onSurfaceVariant,
                                        indicatorColor = c.primary.copy(alpha = 0.12f)
                                    )
                                )
                            }
                        }
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
                    onFileSelected = {}
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
                val vm: EncodeViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                LaunchedEffect(state.stegoText) {
                    state.stegoText?.let {
                        navController.navigate(Routes.ENCODE_RESULT) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                }

                EncodeOptionsScreen(
                    inputText = inputText,
                    secretText = secretText,
                    isLoading = state.isLoading,
                    error = state.error,
                    onBack = {
                        vm.reset()
                        navController.popBackStack()
                    },
                    onEncode = { _, _, _, _, _, _ ->
                        vm.encode(secretText, carrierText = inputText)
                    }
                )
            }
            composable(Routes.ENCODE_RESULT) {
                val vm: EncodeViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                val stego = state.stegoText ?: ""
                EncodeResultScreen(
                    stegoText = stego,
                    onBack = {
                        vm.reset()
                        navController.popBackStack(Routes.HOME, false)
                    },
                    onNew = {
                        vm.reset()
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
                val vm: DecodeViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                LaunchedEffect(state.decodedText) {
                    state.decodedText?.let {
                        navController.navigate(Routes.DECODE_RESULT) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                }

                DecodeOptionsScreen(
                    inputText = inputText,
                    isLoading = state.isLoading,
                    error = state.error,
                    onBack = {
                        vm.reset()
                        navController.popBackStack()
                    },
                    onDecode = { _, _, _ ->
                        vm.decode(inputText)
                    }
                )
            }
            composable(Routes.DECODE_RESULT) {
                val vm: DecodeViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                val text = state.decodedText ?: ""
                DecodeResultScreen(
                    decodedText = text,
                    onBack = {
                        vm.reset()
                        navController.popBackStack(Routes.HOME, false)
                    },
                    onNew = {
                        vm.reset()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    languageCode = languageCode,
                    onChangeLanguage = onChangeLanguage
                )
            }
        }
    }
}
