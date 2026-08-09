package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val c = MaterialTheme.colorScheme
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.history_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Outlined.History, null, Modifier.size(64.dp), tint = c.onSurfaceVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = c.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.history_empty_desc), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
        }
    }
}