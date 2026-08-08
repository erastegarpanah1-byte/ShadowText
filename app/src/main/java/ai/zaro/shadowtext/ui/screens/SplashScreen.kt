package ai.zaro.shadowtext.ui.screens
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val c = MaterialTheme.colorScheme
    var visible by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(0.3f) }
    LaunchedEffect(Unit) { scale = 1f; delay(300); visible = true; delay(1500); onFinished() }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(c.background,c.surface,c.background))), contentAlignment=Alignment.Center) {
        Column(horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(88.dp).scale(scale).clip(CircleShape).background(Brush.linearGradient(listOf(c.primary.copy(alpha=0.2f),c.primary.copy(alpha=0.05f)))), contentAlignment=Alignment.Center) { Icon(Icons.Filled.Shield,null,Modifier.size(48.dp),tint=c.primary) }
            Spacer(Modifier.height(24.dp))
            Text("SHADOWTEXT",style=MaterialTheme.typography.headlineMedium.copy(fontWeight=FontWeight.Bold,letterSpacing=4.sp),color=c.primary)
            Spacer(Modifier.height(8.dp))
            Text("Steganography & Encryption",style=MaterialTheme.typography.bodyLarge.copy(letterSpacing=1.sp),color=c.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Box(Modifier.width(48.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).alpha(if(visible)0.6f else 0f).background(c.primary))
            Spacer(Modifier.height(12.dp))
            AnimatedVisibility(visible=visible) { Text("Secure what matters.",style=MaterialTheme.typography.bodySmall.copy(letterSpacing=1.sp),color=c.onSurfaceVariant.copy(alpha=0.5f),textAlign=TextAlign.Center) }
        }
    }
}
