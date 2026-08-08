package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.screens.*
import ai.zaro.shadowtext.ui.theme.ShadowTextTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val HOME = "home"
    const val ENCODE = "encode"
    const val ENCODE_OPTIONS = "encode_options/{coverText}/{secretText}"
    const val ENCODE_RESULT = "encode_result/{stegoText}"
    const val DECODE = "decode"
    const val DECODE_OPTIONS = "decode_options/{inputText}"
    const val DECODE_RESULT = "decode_result/{decodedText}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    fun encodeOptions(cover: String, secret: String) = "encode_options/$cover/$secret"
    fun encodeResult(stego: String) = "encode_result/$stego"
    fun decodeOptions(input: String) = "decode_options/$input"
    fun decodeResult(decoded: String) = "decode_result/$decoded"
}

data class BottomNavItem(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)
val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.HISTORY, "History", Icons.Filled.History, Icons.Outlined.History),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowTextApp() {
    ShadowTextTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.HISTORY, Routes.SETTINGS)
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface, tonalElevation = 0.dp) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(selected = selected, onClick = {
                                if (currentRoute != item.route) navController.navigate(item.route) { popUpTo(Routes.HOME) { saveState = true }; launchSingleTop = true; restoreState = true }
                            }, icon = { Icon(if (selected) item.selectedIcon else item.unselectedIcon, item.title) },
                                label = { Text(item.title, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)))
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = Routes.HOME, modifier = Modifier.padding(innerPadding)) {
                composable(Routes.HOME) { HomeScreen(onEncodeClick = { navController.navigate(Routes.ENCODE) }, onDecodeClick = { navController.navigate(Routes.DECODE) }) }
                composable(Routes.ENCODE) { EncodeScreen(onBack = { navController.popBackStack() }, onNext = { c, s -> navController.navigate(Routes.encodeOptions(c, s)) }) }
                composable("encode_options/{coverText}/{secretText}", arguments = listOf(navArgument("coverText") { type = NavType.StringType }, navArgument("secretText") { type = NavType.StringType })) { be -> val c = be.arguments?.getString("coverText") ?: ""; val s = be.arguments?.getString("secretText") ?: ""; EncodeOptionsScreen(c, s, { navController.popBackStack() }, { stego -> navController.navigate(Routes.encodeResult(stego)) }) }
                composable("encode_result/{stegoText}", arguments = listOf(navArgument("stegoText") { type = NavType.StringType })) { be -> val s = be.arguments?.getString("stegoText") ?: ""; EncodeResultScreen(s, { navController.popBackStack(Routes.HOME, false) }, { navController.navigate(Routes.ENCODE) { popUpTo(Routes.HOME) { saveState = true }; launchSingleTop = true; restoreState = true } }) }
                composable(Routes.DECODE) { DecodeScreen(onBack = { navController.popBackStack() }, onNext = { input -> navController.navigate(Routes.decodeOptions(input)) }) }
                composable("decode_options/{inputText}", arguments = listOf(navArgument("inputText") { type = NavType.StringType })) { be -> val i = be.arguments?.getString("inputText") ?: ""; DecodeOptionsScreen(i, { navController.popBackStack() }, { decoded -> navController.navigate(Routes.decodeResult(decoded)) }) }
                composable("decode_result/{decodedText}", arguments = listOf(navArgument("decodedText") { type = NavType.StringType })) { be -> val d = be.arguments?.getString("decodedText") ?: ""; DecodeResultScreen(d, { navController.popBackStack(Routes.HOME, false) }, { navController.navigate(Routes.DECODE) { popUpTo(Routes.HOME) { saveState = true }; launchSingleTop = true; restoreState = true } }) }
                composable(Routes.HISTORY) { HistoryScreen() }
                composable(Routes.SETTINGS) { SettingsScreen() }
            }
        }
    }
}
