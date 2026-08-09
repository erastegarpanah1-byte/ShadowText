package ai.zaro.shadowtext.ui.screens

import ai.zaro.shadowtext.R
import ai.zaro.shadowtext.ui.components.ConstrainedColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeInputScreen(onBack: () -> Unit, onNext: (String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var inputText by remember { mutableStateOf("") }
    Scaffold(containerColor = c.background, topBar = { TopAppBar(title = { Text(stringResource(R.string.decode_title), fontWeight = FontWeight.SemiBold, color = c.onBackground) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = c.onBackground) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background)) }) { padding ->
        ConstrainedColumn(modifier = Modifier.padding(padding)) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.decode_input_label), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(inputText, { if (it.length <= 5000) inputText = it }, Modifier.fillMaxWidth().heightIn(min = 200.dp), placeholder = { Text(stringResource(R.string.decode_input_placeholder), color = c.onSurfaceVariant.copy(alpha = 0.4f)) }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = c.secondary, unfocusedBorderColor = c.outline, focusedContainerColor = c.surfaceVariant.copy(alpha = 0.3f), unfocusedContainerColor = c.surfaceVariant.copy(alpha = 0.3f)))
            Text("${inputText.length}/5000", style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp, end = 4.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onNext(inputText) }, enabled = inputText.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = c.secondary, contentColor = c.onSecondary)) { Text(stringResource(R.string.encode_next), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
