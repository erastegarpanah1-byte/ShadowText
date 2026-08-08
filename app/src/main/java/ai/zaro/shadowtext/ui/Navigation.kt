package ai.zaro.shadowtext.ui

import ai.zaro.shadowtext.ui.screens.DecodeScreen
import ai.zaro.shadowtext.ui.screens.EncodeScreen
import ai.zaro.shadowtext.ui.screens.HomeScreen
import ai.zaro.shadowtext.ui.screens.ResultScreen
import ai.zaro.shadowtext.ui.settings.SettingsManager
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

object Routes {
    const val HOME = "home"
    const val ENCODE = "encode"
    const val DECODE = "decode"
    const val RESULT = "result/{mode}/{stegoText}"
    const val SETTINGS = "settings"
}

private data class MenuItem(val id: String, val title: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowTextNavHost(modifier: Modifier = Modifier, intent: Intent?, settings: SettingsManager? = null) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItem("home", "Home", Icons.Outlined.Home),
        MenuItem("encode", "Encode", Icons.Outlined.Lock),
        MenuItem("decode", "Decode", Icons.Outlined.Search),
        MenuItem("settings", "Settings", Icons.Outlined.Settings),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(290.dp),
                drawerContainerColor = ShadoColors.DrawerBg
            ) {
                // Drawer Header
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(ShadoColors.BgSurface, ShadoColors.DrawerBg)))
                        .padding(24.dp)
                ) {
                    Column {
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ShadoColors.Accent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Shield, null, Modifier.size(28.dp), tint = ShadoColors.Accent)
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "ShadowText",
                            color = ShadoColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Steganography Engine",
                            color = ShadoColors.TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                HorizontalDivider(color = ShadoColors.BorderSubtle, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))

                // Navigation Label
                Text(
                    "NAVIGATION",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                    color = ShadoColors.TextMuted,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                menuItems.forEach { item ->
                    val selected = when (item.id) {
                        "home" -> navController.currentBackStackEntry?.destination?.route == Routes.HOME
                        else -> false
                    }
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                item.icon,
                                null,
                                tint = if (item.id == "home") ShadoColors.Accent else ShadoColors.TextSecondary
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                color = if (item.id == "home") ShadoColors.Accent else ShadoColors.TextSecondary,
                                fontWeight = if (item.id == "home") FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            when (item.id) {
                                "home" -> navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                                "encode" -> navController.navigate(Routes.ENCODE)
                                "decode" -> navController.navigate(Routes.DECODE)
                                "settings" -> navController.navigate(Routes.SETTINGS)
                            }
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            selectedContainerColor = ShadoColors.Accent.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                HorizontalDivider(color = ShadoColors.BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                // About
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Info, null, tint = ShadoColors.TextMuted) },
                    label = { Text("About", color = ShadoColors.TextMuted) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(Modifier.weight(1f))

                // Version
                Text(
                    "v1.0.0-alpha",
                    style = MaterialTheme.typography.labelSmall,
                    color = ShadoColors.TextDisabled,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Routes.HOME, modifier = modifier) {
            composable(Routes.HOME) {
                HomeScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
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
                    onEncodeComplete = { stegoText ->
                        val e = java.net.URLEncoder.encode(stegoText, "UTF-8")
                        navController.navigate("result/encoded/$e")
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
            ) { be ->
                val m = be.arguments?.getString("mode") ?: "encoded"
                val s = be.arguments?.getString("stegoText") ?: ""
                ResultScreen(
                    mode = m,
                    stegoText = s,
                    onNavigateBack = { navController.popBackStack(Routes.HOME, inclusive = false) },
                    onNavigateHome = { navController.popBackStack(Routes.HOME, inclusive = true) }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(settings = settings, onBack = { navController.popBackStack() })
            }
        }
    }
}

// ═══ Settings Screen ═══

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settings: SettingsManager?, onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = ShadoColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ShadoColors.Accent) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadoColors.BgDarker)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(ShadoColors.BgDarker, ShadoColors.BgDark)))
                .padding(padding)
                .padding(horizontal = ShadoDimens.paddingScreen)
        ) {
            Spacer(Modifier.height(8.dp))

            // ═══ General Section ═══
            SectionTitle("GENERAL")
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsToggleRow(
                    label = "Theme",
                    value = if (settings?.isDarkMode == true) "Dark" else "Light",
                    icon = Icons.Outlined.DarkMode,
                    checked = settings?.isDarkMode ?: true,
                    onToggle = { settings?.setDarkMode(it) }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Language",
                    value = if (settings?.localeCode == "fa") "Persian" else "English",
                    icon = Icons.Outlined.Language
                )
                SettingsDivider()
                SettingsRow(
                    label = "Default Carrier Text",
                    value = "Auto",
                    icon = Icons.Outlined.Article
                )
            }

            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsToggleRow(
                    label = "Auto Detect",
                    value = if (true) "On" else "Off",
                    icon = Icons.Outlined.Search,
                    subtitle = "Automatically detect hidden data",
                    checked = true,
                    onToggle = { }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Save History",
                    value = if (true) "On" else "Off",
                    icon = Icons.Outlined.History,
                    subtitle = "Save encode/decode history",
                    checked = true,
                    onToggle = { }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ═══ Advanced Section ═══
            SectionTitle("ADVANCED")
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsRow(
                    label = "Encoding Mode",
                    value = "ZWC",
                    icon = Icons.Outlined.Code
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Compression",
                    value = "Enabled",
                    icon = Icons.Outlined.Compress,
                    subtitle = "Compress data before encoding",
                    checked = true,
                    onToggle = { }
                )
                SettingsDivider()
                SettingsRow(
                    label = "Chunk Size",
                    value = "Medium",
                    icon = Icons.Outlined.Storage
                )
            }

            Spacer(Modifier.height(16.dp))

            // ═══ About Section ═══
            SectionTitle("ABOUT")
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingsRow(
                    label = "Version",
                    value = "1.0.0",
                    icon = Icons.Outlined.Info
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
        color = ShadoColors.TextMuted,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadoColors.BgCard),
        shape = RoundedCornerShape(ShadoDimens.cornerMd),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShadoColors.BorderSubtle)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = ShadoColors.BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun SettingsRow(label: String, value: String, icon: ImageVector) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(20.dp), tint = ShadoColors.TextMuted)
            Spacer(Modifier.width(10.dp))
            Text(label, color = ShadoColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Text(value, color = ShadoColors.TextPrimary, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    value: String,
    icon: ImageVector,
    subtitle: String? = null,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, Modifier.size(20.dp), tint = ShadoColors.TextMuted)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, color = ShadoColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                if (subtitle != null) {
                    Text(subtitle, color = ShadoColors.TextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ShadoColors.BgDark,
                checkedTrackColor = ShadoColors.Accent,
                uncheckedThumbColor = ShadoColors.TextMuted,
                uncheckedTrackColor = ShadoColors.Border
            )
        )
    }
}
