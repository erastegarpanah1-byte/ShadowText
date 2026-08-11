package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_about), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = c.onBackground) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.settings_about_tagline), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = c.primary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.settings_about_intro), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.settings_about_intro_body), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(Modifier.height(28.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.settings_about_privacy_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.settings_about_privacy_body), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, lineHeight = 22.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.settings_about_features_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.settings_about_features), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, lineHeight = 22.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = c.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.settings_about_note_title), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = c.error.copy(alpha = 0.8f))
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.settings_about_note_body), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, lineHeight = 22.sp)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text("ShadowText", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = c.primary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.settings_version), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.settings_about_footer), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(40.dp))
        }
    }
}
