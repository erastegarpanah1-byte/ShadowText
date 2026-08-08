package ai.zaro.shadowtext.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodeInputScreen(onBack: () -> Unit, onNext: (String, String) -> Unit) {
    val c = MaterialTheme.colorScheme
    var inputText by remember { mutableStateOf("") }
    var secretText by remember { mutableStateOf("") }
    Scaffold(containerColor=c.background, topBar={TopAppBar(title={Text("Encode",fontWeight=FontWeight.SemiBold,color=c.onBackground)},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back",tint=c.onBackground)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=c.background))}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=24.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("Input (Text)",style=MaterialTheme.typography.labelLarge.copy(fontWeight=FontWeight.SemiBold),color=c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(inputText,{if(it.length<=5000)inputText=it},Modifier.fillMaxWidth().heightIn(min=120.dp),placeholder={Text("Paste or type your cover text here...",color=c.onSurfaceVariant.copy(alpha=0.4f))},shape=RoundedCornerShape(14.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=c.primary,unfocusedBorderColor=c.outline,focusedContainerColor=c.surfaceVariant.copy(alpha=0.3f),unfocusedContainerColor=c.surfaceVariant.copy(alpha=0.3f)))
            Text("${"$"}{inputText.length}/5000",style=MaterialTheme.typography.labelSmall,color=c.onSurfaceVariant.copy(alpha=0.5f),modifier=Modifier.padding(top=4.dp,end=4.dp))
            Spacer(Modifier.height(20.dp))
            Text("Secret Text",style=MaterialTheme.typography.labelLarge.copy(fontWeight=FontWeight.SemiBold),color=c.onBackground)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(secretText,{if(it.length<=2000)secretText=it},Modifier.fillMaxWidth().heightIn(min=120.dp),placeholder={Text("Type your secret text here...",color=c.onSurfaceVariant.copy(alpha=0.4f))},shape=RoundedCornerShape(14.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=c.primary,unfocusedBorderColor=c.outline,focusedContainerColor=c.surfaceVariant.copy(alpha=0.3f),unfocusedContainerColor=c.surfaceVariant.copy(alpha=0.3f)))
            Text("${"$"}{secretText.length}/2000",style=MaterialTheme.typography.labelSmall,color=c.onSurfaceVariant.copy(alpha=0.5f),modifier=Modifier.padding(top=4.dp,end=4.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick={onNext(inputText,secretText)},enabled=inputText.isNotBlank()&&secretText.isNotBlank(),modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=c.primary,contentColor=c.onPrimary)){Text("Next",fontWeight=FontWeight.SemiBold,style=MaterialTheme.typography.titleSmall)}
            Spacer(Modifier.height(24.dp))
        }
    }
}
