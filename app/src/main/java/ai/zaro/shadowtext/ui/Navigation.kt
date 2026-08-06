package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.screens.DecodeScreen
import ai.zaro.shadowtext.ui.screens.EncodeScreen
import ai.zaro.shadowtext.ui.screens.HomeScreen
import ai.zaro.shadowtext.ui.screens.ResultScreen
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val HOME = "home"
    const val ENCODE = "encode"
    const val DECODE = "decode"
    const val RESULT = "result/{mode}/{stegoText}"
}

@Composable
fun ShadowTextNavHost(
    modifier: Modifier = Modifier,
    intent: Intent?,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME, modifier = modifier) {
        composable(Routes.HOME) {
            HomeScreen(
                onEncodeClick = { navController.navigate(Routes.ENCODE) },
                onDecodeClick = { navController.navigate(Routes.DECODE) },
                incomingIntent = intent,
                onNavigateToDecode = { text ->
                    navController.navigate(Routes.DECODE)
                }
            )
        }
        composable(Routes.ENCODE) {
            EncodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onEncodeComplete = { stegoText ->
                    val encoded = java.net.URLEncoder.encode(stegoText, "UTF-8")
                    navController.navigate("result/encoded/$encoded")
                }
            )
        }
        composable(Routes.DECODE) {
            DecodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onDecodeComplete = { _ -> navController.navigate("result/decoded/") }
            )
        }
        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("stegoText") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "encoded"
            val stegoText = backStackEntry.arguments?.getString("stegoText") ?: ""
            ResultScreen(
                mode = mode, stegoText = stegoText,
                onNavigateBack = { navController.popBackStack(Routes.HOME, false) },
                onNavigateHome = { navController.popBackStack(Routes.HOME, true) }
            )
        }
    }
}
