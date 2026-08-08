package ai.zaro.shadowtext.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Gold = Color(0xFFD4A574)
private val GoldLight = Color(0xFFFFE5C8)
private val NavySurface = Color(0xFF0D1625)
private val NavyCard = Color(0xFF111D30)
private val NavyBorder = Color(0xFF1E3050)
private val TealAccent = Color(0xFF2ED4B4)
private val DimWhite = Color(0xFFC1C6CF)

@Composable
fun HomeScreen(onMenuClick: () -> Unit = {}, onEncodeClick: () -> Unit, onDecodeClick: () -> Unit, incomingIntent: Intent?, onNavigateToDecode: (String) -> Unit) {
    LaunchedEffect(incomingIntent) { if (incomingIntent?.action == Intent.ACTION_SEND) { incomingIntent.getStringExtra(Intent.EXTRA_TEXT)?.takeIf { it.isNotBlank() }?.let(onNavigateToDecode) } }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070E17), NavySurface, Color(0xFF0A1A2E))))) {
        IconButton(onClick = onMenuClick, modifier = Modifier.statusBarsPadding().padding(12.dp)) { Icon(Icons.Filled.Menu, "Menu", Modifier.size(28.dp), tint = Gold) }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(56.dp))
            Box(Modifier.size(80.dp).shadow(20.dp, CircleShape).background(Brush.linearGradient(listOf(Color(0xFF1A3550), Color(0xFF0D2238)), Offset.Zero, Offset.Infinite), CircleShape).border(1.5.dp, NavyBorder, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Shield, null, Modifier.size(42.dp), tint = Gold) }
            Spacer(Modifier.height(20.dp))
            Text("SHADOWTEXT", style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp, shadow = Shadow(color = Gold.copy(alpha = 0.4f), blurRadius = 20f)), color = GoldLight)
            Spacer(Modifier.height(4.dp)); Text("Offline Text Steganography", style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 1.5.sp), color = Gold.copy(alpha = 0.6f))
            Spacer(Modifier.height(6.dp)); Box(Modifier.width(60.dp).height(2.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Gold.copy(alpha = 0.5f), Color.Transparent))))
            Spacer(Modifier.height(36.dp))
            FeatureCard("ENCODE", "Hide any file inside ordinary text", Icons.Filled.Lock, Gold, listOf("Select any file from your device", "Generate invisible stego text", "Copy, share, or send anywhere"), onEncodeClick)
            Spacer(Modifier.height(20.dp))
            FeatureCard("DECODE", "Extract hidden files from text", Icons.Outlined.Search, TealAccent, listOf("Paste any text containing hidden data", "Auto-detect encoding scheme", "Extract original file instantly"), onDecodeClick)
            Spacer(Modifier.height(36.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2440).copy(alpha = 0.6f)), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder.copy(alpha = 0.5f))) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Security, null, Modifier.size(36.dp), tint = TealAccent); Spacer(Modifier.width(14.dp)); Column { Text("Fully Offline & Private", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = DimWhite); Text("No servers. No tracking. No permissions needed.", style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.6f)) } } }
            Spacer(Modifier.height(16.dp))
            Text("IMAGES \u2022 VIDEO \u2022 AUDIO \u2022 PDF \u2022 ZIP \u2022 APK \u2022 ANY BINARY", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp), color = NavyBorder, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp)); Text("v1.0.0-alpha \u2022 Phase 1", style = MaterialTheme.typography.labelSmall, color = NavyBorder.copy(alpha = 0.6f))
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: ImageVector, accentColor: Color, features: List<String>, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = accentColor.copy(alpha = 0.15f), spotColor = accentColor.copy(alpha = 0.1f)).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(NavyCard, Color(0xFF142840)), Offset(0f, 0f), Offset(1000f, 1000f))).border(1.dp, Brush.linearGradient(listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha = 0.08f)), Offset(0f, 0f), Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)), RoundedCornerShape(20.dp))) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(accentColor.copy(alpha = 0.12f)).border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(26.dp), tint = accentColor) }; Spacer(Modifier.width(16.dp)); Column { Text(title, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp), color = accentColor); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = DimWhite.copy(alpha = 0.6f)) } }
                Spacer(Modifier.height(18.dp)); features.forEach { f -> Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.CheckCircle, null, Modifier.size(16.dp), tint = accentColor.copy(alpha = 0.5f)); Spacer(Modifier.width(10.dp)); Text(f, style = MaterialTheme.typography.bodyMedium, color = DimWhite.copy(alpha = 0.75f)) } }
                Spacer(Modifier.height(16.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) { Text("Open", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp), color = accentColor); Spacer(Modifier.width(6.dp)); Icon(Icons.Filled.ArrowForward, null, Modifier.size(18.dp), tint = accentColor) }
            }
        }
    }
}
