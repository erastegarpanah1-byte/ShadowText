package ai.zaro.shadowtext.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onEncodeClick: () -> Unit, onDecodeClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(32.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Shield, null, Modifier.size(56.dp), tint = c.primary) }
        Spacer(Modifier.height(12.dp))
        Text("Steganography\n& Encryption", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = c.onBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(40.dp))
        Card(onClick = onEncodeClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = c.primary), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(Modifier.padding(24.dp)) {
                Icon(Icons.Filled.Lock, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.height(16.dp))
                Text("Encode", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Hide your secret text inside text", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            }
        }
        Spacer(Modifier.height(20.dp))
        Card(onClick = onDecodeClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), border = CardDefaults.outlinedCardBorder(), elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.padding(24.dp)) {
                Icon(Icons.Filled.Search, null, Modifier.size(40.dp), tint = c.primary)
                Spacer(Modifier.height(16.dp))
                Text("Decode", style = MaterialTheme.typography.titleLarge, color = c.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Extract hidden text from text", style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(40.dp))
        Text("Secure what matters.", style = MaterialTheme.typography.bodySmall, color = c.onBackground.copy(alpha = 0.4f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), letterSpacing = 1.sp)
        Spacer(Modifier.height(24.dp))
    }
}
