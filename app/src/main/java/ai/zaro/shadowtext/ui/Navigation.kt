package ai.zaro.shadowtext.ui

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
    const val RESULT = "result/{mode}"
    const val DETAILS = "details"
}

@Composable
fun ShadowTextNavHost(
    modifier: Modifier = Modifier,
    intent: Intent?,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onEncodeClick = { navController.navigate(Routes.ENCODE) },
                onDecodeClick = { navController.navigate(Routes.DECODE) },
                incomingIntent = intent,
                onNavigateToDecode = { text ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("sharedText", text)
                    navController.navigate(Routes.DECODE)
                }
            )
        }

        composable(Routes.ENCODE) {
            EncodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onEncodeComplete = { result ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("encodeResult", result)
                    navController.navigate("result/encoded")
                }
            )
        }

        composable(Routes.DECODE) {
            DecodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onDecodeComplete = { result ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("decodeResult", result)
                    navController.navigate("result/decoded")
                }
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "encoded"

            ResultScreen(
                mode = mode,
                onNavigateBack = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onNavigateHome = {
                    navController.popBackStack(Routes.HOME, inclusive = true)
                }
            )
        }
    }
}
