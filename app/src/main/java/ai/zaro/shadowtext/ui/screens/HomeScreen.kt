package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onEncodeClick: () -> Unit, onDecodeClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(20.dp))
        Box(Modifier.size(64.dp).clip(CircleShape).background(c.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Shield, null, Modifier.size(36.dp), tint = c.primary) }
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.home_heading), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 32.sp), color = c.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        // Encode card
        Card(onClick = onEncodeClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = c.primary), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, Modifier.size(36.dp), tint = c.onPrimary)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.home_encode), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = c.onPrimary)
                    Text(stringResource(R.string.home_encode_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onPrimary.copy(alpha = 0.8f))
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(20.dp), tint = c.onPrimary.copy(alpha = 0.7f))
            }
        }
        Spacer(Modifier.height(16.dp))
        // Decode card
        OutlinedCard(onClick = onDecodeClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.outlinedCardColors(containerColor = c.surface), border = CardDefaults.outlinedCardBorder()) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(c.secondary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Search, null, Modifier.size(26.dp), tint = c.secondary) }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.home_decode), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = c.onSurface)
                    Text(stringResource(R.string.home_decode_subtitle), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(20.dp), tint = c.secondary)
            }
        }
        Spacer(Modifier.height(40.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Feat(Icons.Outlined.TextFields, stringResource(R.string.home_text_in_text))
            Feat(Icons.Outlined.InsertDriveFile, stringResource(R.string.home_text_in_file))
            Feat(Icons.Outlined.Storage, stringResource(R.string.home_large_files))
            Feat(Icons.Outlined.Shield, stringResource(R.string.home_secure))
        }
        Spacer(Modifier.height(32.dp))
        Text(stringResource(R.string.home_security_first), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp), color = c.onSurfaceVariant.copy(alpha = 0.4f))
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.home_processing), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.5f), textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.home_encryption), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.35f), textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Feat(icon: ImageVector, label: String) {
    val c = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, Modifier.size(22.dp), tint = c.onSurfaceVariant.copy(alpha = 0.5f)); Spacer(Modifier.height(4.dp)); Text(label, style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant.copy(alpha = 0.5f)) }
}