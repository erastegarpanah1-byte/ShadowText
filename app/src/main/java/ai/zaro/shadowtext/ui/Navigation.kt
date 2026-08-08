package ai.zaro.shadowtext.ui
import ai.zaro.shadowtext.ui.screens.DecodeScreen
import ai.zaro.shadowtext.ui.screens.EncodeScreen
import ai.zaro.shadowtext.ui.screens.HomeScreen
import ai.zaro.shadowtext.ui.screens.ResultScreen
import ai.zaro.shadowtext.ui.settings.SettingsManager
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
object Routes{const val HOME="home";const val ENCODE="encode";const val DECODE="decode";const val RESULT="result/{mode}/{stegoText}";const val SETTINGS="settings"}
private data class MI(val id:String,val title:String,val icon:ImageVector)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowTextNavHost(modifier:Modifier=Modifier,intent:Intent?,settings:SettingsManager?=null){
val nc=rememberNavController();val ds=rememberDrawerState(initialValue=DrawerValue.Closed);val sc=rememberCoroutineScope()
val items=listOf(MI("home","Home",Icons.Outlined.Home),MI("encode","Encode",Icons.Outlined.Lock),MI("decode","Decode",Icons.Outlined.Search),MI("settings","Settings",Icons.Outlined.Settings))
ModalNavigationDrawer(drawerState=ds,drawerContent={
ModalDrawerSheet(modifier=Modifier.width(290.dp),drawerContainerColor=ShadoColors.DrawerBg){
Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(ShadoColors.BgSurface,ShadoColors.DrawerBg))).padding(24.dp)){Column{Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(ShadoColors.Accent.copy(alpha=0.12f)),contentAlignment=Alignment.Center){Icon(Icons.Filled.Shield,null,Modifier.size(28.dp),tint=ShadoColors.Accent)};Spacer(Modifier.height(14.dp));Text("ShadowText",color=ShadoColors.TextPrimary,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge);Text("Steganography Engine",color=ShadoColors.TextMuted,style=MaterialTheme.typography.bodySmall)}}
Divider(color=ShadoColors.BorderSubtle,thickness=0.5.dp);Spacer(Modifier.height(8.dp))
Text("NAVIGATION",style=MaterialTheme.typography.labelSmall.copy(letterSpacing=1.5.sp),color=ShadoColors.TextMuted,modifier=Modifier.padding(horizontal=24.dp,vertical=8.dp))
items.forEach{itm->NavigationDrawerItem(icon={Icon(itm.icon,null,tint=if(itm.id=="home")ShadoColors.Accent else ShadoColors.TextSecondary)},label={Text(itm.title,color=if(itm.id=="home")ShadoColors.Accent else ShadoColors.TextSecondary,fontWeight=if(itm.id=="home")FontWeight.SemiBold else FontWeight.Normal)},selected=itm.id=="home",onClick={when(itm.id){"home"->nc.navigate(Routes.HOME){popUpTo(Routes.HOME){inclusive=true}};"encode"->nc.navigate(Routes.ENCODE);"decode"->nc.navigate(Routes.DECODE);"settings"->nc.navigate(Routes.SETTINGS)};sc.launch{ds.close()}},colors=NavigationDrawerItemDefaults.colors(unselectedContainerColor=Color.Transparent,selectedContainerColor=ShadoColors.Accent.copy(alpha=0.08f)),modifier=Modifier.padding(horizontal=12.dp))}
Divider(color=ShadoColors.BorderSubtle,thickness=0.5.dp,modifier=Modifier.padding(vertical=8.dp))
NavigationDrawerItem(icon={Icon(Icons.Outlined.Info,null,tint=ShadoColors.TextMuted)},label={Text("About",color=ShadoColors.TextMuted)},selected=false,onClick={sc.launch{ds.close()}},colors=NavigationDrawerItemDefaults.colors(unselectedContainerColor=Color.Transparent),modifier=Modifier.padding(horizontal=12.dp))
Spacer(Modifier.weight(1f));Text("v1.0.0-alpha",style=MaterialTheme.typography.labelSmall,color=ShadoColors.TextDisabled,modifier=Modifier.padding(24.dp))
}
}){
NavHost(navController=nc,startDestination=Routes.HOME,modifier=modifier){
composable(Routes.HOME){HomeScreen(onMenuClick={sc.launch{ds.open()}},onEncodeClick={nc.navigate(Routes.ENCODE)},onDecodeClick={nc.navigate(Routes.DECODE)},incomingIntent=intent,onNavigateToDecode={text->nc.currentBackStackEntry?.savedStateHandle?.set("sharedText",text);nc.navigate(Routes.DECODE)})}
composable(Routes.ENCODE){EncodeScreen(onNavigateBack={nc.popBackStack()},onEncodeComplete={st->val e=java.net.URLEncoder.encode(st,"UTF-8");nc.navigate("result/encoded/$e")})}
composable(Routes.DECODE){DecodeScreen(onNavigateBack={nc.popBackStack()},onDecodeComplete={_->nc.navigate("result/decoded/")})}
composable(route=Routes.RESULT,arguments=listOf(navArgument("mode"){type=NavType.StringType},navArgument("stegoText"){type=NavType.StringType;defaultValue=""})){be->val m=be.arguments?.getString("mode")?:"encoded";val s=be.arguments?.getString("stegoText")?:"";ResultScreen(mode=m,stegoText=s,onNavigateBack={nc.popBackStack(Routes.HOME,inclusive=false)},onNavigateHome={nc.popBackStack(Routes.HOME,inclusive=true)})}
composable(Routes.SETTINGS){SettingsScreen(settings=settings,onBack={nc.popBackStack()})}}}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settings:SettingsManager?,onBack:()->Unit){Scaffold(containerColor=Color.Transparent,topBar={TopAppBar(title={Text("Settings",color=ShadoColors.TextPrimary,fontWeight=FontWeight.Bold)},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=ShadoColors.Accent)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=ShadoColors.BgDarker))}){padding->Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ShadoColors.BgDarker,ShadoColors.BgDark))).padding(padding).padding(horizontal=16.dp)){Spacer(Modifier.height(8.dp));St("GENERAL");Spacer(Modifier.height(8.dp));SC{STgl("Theme",if(settings?.isDarkMode==true)"Dark"else"Light",Icons.Outlined.DarkMode,checked=settings?.isDarkMode?:true,onToggle={settings?.setDarkMode(it)});SD();SRw("Language",if(settings?.localeCode=="fa")"Persian"else"English",Icons.Outlined.Language);SD();SRw("Default Carrier Text","Auto",Icons.Outlined.Article)};Spacer(Modifier.height(8.dp));SC{STgl("Auto Detect","On",Icons.Outlined.Search,"Automatically detect hidden data",true,{});SD();STgl("Save History","On",Icons.Outlined.History,"Save encode/decode history",true,{})};Spacer(Modifier.height(16.dp));St("ADVANCED");Spacer(Modifier.height(8.dp));SC{SRw("Encoding Mode","ZWC",Icons.Outlined.Code);SD();STgl("Compression","Enabled",Icons.Outlined.Compress,"Compress data before encoding",true,{});SD();SRw("Chunk Size","Medium",Icons.Outlined.Storage)};Spacer(Modifier.height(16.dp));St("ABOUT");Spacer(Modifier.height(8.dp));SC{SRw("Version","1.0.0",Icons.Outlined.Info)};Spacer(Modifier.height(32.dp))}}
@Composable private fun St(t:String){Text(t,style=MaterialTheme.typography.labelSmall.copy(letterSpacing=1.5.sp),color=ShadoColors.TextMuted,modifier=Modifier.padding(vertical=4.dp))}
@Composable private fun SC(content:@Composable ColumnScope.()->Unit){Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=ShadoColors.BgCard),shape=RoundedCornerShape(14.dp),border=androidx.compose.foundation.BorderStroke(1.dp,ShadoColors.BorderSubtle)){Column(Modifier.padding(horizontal=16.dp,vertical=4.dp),content=content)}}
@Composable private fun SD(){Divider(color=ShadoColors.BorderSubtle,modifier=Modifier.padding(vertical=4.dp))}
@Composable private fun SRw(l:String,v:String,ic:ImageVector){Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){Row(verticalAlignment=Alignment.CenterVertically){Icon(ic,null,Modifier.size(20.dp),tint=ShadoColors.TextMuted);Spacer(Modifier.width(10.dp));Text(l,color=ShadoColors.TextSecondary,style=MaterialTheme.typography.bodyMedium)};Text(v,color=ShadoColors.TextPrimary,fontWeight=FontWeight.Medium,style=MaterialTheme.typography.labelMedium)}}
@Composable private fun STgl(l:String,v:String,ic:ImageVector,sub:String?=null,checked:Boolean,onToggle:(Boolean)->Unit){Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.weight(1f)){Icon(ic,null,Modifier.size(20.dp),tint=ShadoColors.TextMuted);Spacer(Modifier.width(10.dp));Column{Text(l,color=ShadoColors.TextSecondary,style=MaterialTheme.typography.bodyMedium);if(sub!=null)Text(sub,color=ShadoColors.TextMuted,style=MaterialTheme.typography.labelSmall)}};Switch(checked=checked,onCheckedChange=onToggle,colors=SwitchDefaults.colors(checkedThumbColor=ShadoColors.BgDark,checkedTrackColor=ShadoColors.Accent,uncheckedThumbColor=ShadoColors.TextMuted,uncheckedTrackColor=ShadoColors.Border))}}
