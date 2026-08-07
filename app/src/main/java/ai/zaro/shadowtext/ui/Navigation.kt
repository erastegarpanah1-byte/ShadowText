package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.screens.DecodeScreen
import ai.zaro.shadowtext.ui.screens.EncodeScreen
import ai.zaro.shadowtext.ui.screens.HomeScreen
import ai.zaro.shadowtext.ui.screens.ResultScreen
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val ENCODE = "encode"
    const val DECODE = "decode"
    const val RESULT = "result/{mode}/{stegoText}"
}

private val Gold = Color(0xFFD4A574)
private val NavyBorder = Color(0xFF1E3050)
private val DimWhite = Color(0xFFC1C6CF)

private data class MenuItem(val id: String, val title: String, val icon: ImageVector, val subtitle: String? = null)
private data class MenuSection(val title: String, val items: List<MenuItem>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowTextNavHost(modifier: Modifier = Modifier, intent: Intent?) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuSections = listOf(
        MenuSection("Navigation", listOf(MenuItem("home","Home",Icons.Outlined.Home,"Main screen"),MenuItem("encode","Encode",Icons.Outlined.Lock,"Hide data in text"),MenuItem("decode","Decode",Icons.Outlined.Search,"Extract hidden data"))),
        MenuSection("Settings", listOf(MenuItem("settings_encoding","Encoding Settings",Icons.Outlined.Tune,"Encoding defaults"),MenuItem("settings_appearance","Appearance",Icons.Outlined.Palette,"Theme & colors"),MenuItem("settings_about","About",Icons.Outlined.Info,"Version & info"))),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp), drawerContainerColor = Color(0xFF0A1628)) {
                Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF0D2238),Color(0xFF0A1628)))).padding(24.dp)) {
                    Column { Icon(Icons.Filled.Shield,null,Modifier.size(38.dp),tint=Gold); Spacer(Modifier.height(12.dp)); Text("SHADOWTEXT",color=Gold,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge); Text("Steganography Engine",color=DimWhite.copy(alpha=0.5f),style=MaterialTheme.typography.bodySmall) }
                }
                Spacer(Modifier.height(8.dp)); Divider(color=NavyBorder,thickness=0.5.dp)
                menuSections.forEach { section ->
                    Spacer(Modifier.height(8.dp)); Text(section.title.uppercase(),style=MaterialTheme.typography.labelSmall.copy(letterSpacing=1.2.sp),color=NavyBorder,modifier=Modifier.padding(horizontal=24.dp,vertical=4.dp))
                    section.items.forEach { item ->
                        NavigationDrawerItem(icon={Icon(item.icon,null,tint=if(item.id=="home")Gold else DimWhite.copy(alpha=0.7f))},label={Column{Text(item.title,color=DimWhite,style=MaterialTheme.typography.bodyMedium);item.subtitle?.let{Text(it,style=MaterialTheme.typography.labelSmall,color=DimWhite.copy(alpha=0.4f))}}},selected=false,
                            onClick={
                                when{item.id=="home"->navController.navigate(Routes.HOME){popUpTo(Routes.HOME){inclusive=true}};item.id=="encode"->navController.navigate(Routes.ENCODE);item.id=="decode"->navController.navigate(Routes.DECODE)}
                                scope.launch{drawerState.close()}
                            },colors=NavigationDrawerItemDefaults.colors(unselectedContainerColor=Color.Transparent),modifier=Modifier.padding(horizontal=12.dp))
                    }
                }
            }
        },
    ) {
        NavHost(navController=navController,startDestination=Routes.HOME,modifier=modifier) {
            composable(Routes.HOME){HomeScreen(onMenuClick={scope.launch{drawerState.open()}},onEncodeClick={navController.navigate(Routes.ENCODE)},onDecodeClick={navController.navigate(Routes.DECODE)},incomingIntent=intent,onNavigateToDecode={text->navController.currentBackStackEntry?.savedStateHandle?.set("sharedText",text);navController.navigate(Routes.DECODE)})}
            composable(Routes.ENCODE){EncodeScreen(onNavigateBack={navController.popBackStack()},onEncodeComplete={stegoText->val encoded=java.net.URLEncoder.encode(stegoText,"UTF-8");navController.navigate("result/encoded/$encoded")})}
            composable(Routes.DECODE){DecodeScreen(onNavigateBack={navController.popBackStack()},onDecodeComplete={_->navController.navigate("result/decoded/")})}
            composable(route=Routes.RESULT,arguments=listOf(navArgument("mode"){type=NavType.StringType},navArgument("stegoText"){type=NavType.StringType;defaultValue=""})){backStackEntry->val mode=backStackEntry.arguments?.getString("mode")?:"encoded";val stegoText=backStackEntry.arguments?.getString("stegoText")?:"";ResultScreen(mode=mode,stegoText=stegoText,onNavigateBack={navController.popBackStack(Routes.HOME,inclusive=false)},onNavigateHome={navController.popBackStack(Routes.HOME,inclusive=true)})}
        }
    }
}
