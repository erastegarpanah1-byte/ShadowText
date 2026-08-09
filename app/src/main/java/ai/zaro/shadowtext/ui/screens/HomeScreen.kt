package ai.zaro.shadowtext.ui.screens
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onEncodeClick: () -> Unit, onDecodeClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
        Spacer(Modifier.height(20.dp))
        Box(Modifier.size(64.dp).clip(CircleShape).background(c.primary.copy(alpha=0.1f)),contentAlignment=Alignment.Center){Icon(Icons.Outlined.Shield,null,Modifier.size(36.dp),tint=c.primary)}
        Spacer(Modifier.height(16.dp))
        Text("Steganography\n& Encryption",style=MaterialTheme.typography.headlineMedium.copy(fontWeight=FontWeight.Bold,lineHeight=32.sp),color=c.onBackground,textAlign=TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        ShadowCard(Icons.Filled.Lock,"Encode","Hide your secret text inside text",c.primary,onEncodeClick,Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedCard(onClick=onDecodeClick,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(20.dp),colors=CardDefaults.outlinedCardColors(containerColor=c.surface),border=CardDefaults.outlinedCardBorder()){Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(c.secondary.copy(alpha=0.1f)),contentAlignment=Alignment.Center){Icon(Icons.Filled.Search,null,Modifier.size(26.dp),tint=c.secondary)};Spacer(Modifier.width(16.dp));Column(Modifier.weight(1f)){Text("Decode",style=MaterialTheme.typography.titleLarge.copy(fontWeight=FontWeight.Bold),color=c.onSurface);Text("Extract hidden text from text",style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant)};Icon(Icons.AutoMirrored.Filled.ArrowForward,null,Modifier.size(20.dp),tint=c.secondary)}}
        Spacer(Modifier.height(40.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){Feat(Icons.Outlined.TextFields,"Text in Text");Feat(Icons.Outlined.InsertDriveFile,"Text in File");Feat(Icons.Outlined.Storage,"Large Files");Feat(Icons.Outlined.Shield,"Secure & Private")}
        Spacer(Modifier.height(32.dp))
        Text("SECURITY FIRST",style=MaterialTheme.typography.labelSmall.copy(fontWeight=FontWeight.Bold,letterSpacing=2.sp),color=c.onSurfaceVariant.copy(alpha=0.4f))
        Spacer(Modifier.height(4.dp))
        Text("All processing is done on your device.\nYour data never leaves your device.",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.5f),textAlign=TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text("Strong encryption + advanced steganography\nkeeps your data safe.",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant.copy(alpha=0.35f),textAlign=TextAlign.Center)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ShadowCard(icon: ImageVector, title: String, subtitle: String, accentColor: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(onClick=onClick,modifier=modifier,shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=accentColor),elevation=CardDefaults.cardElevation(defaultElevation=4.dp)) {
        Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,Modifier.size(36.dp),tint=MaterialTheme.colorScheme.onPrimary);Spacer(Modifier.width(16.dp));Column(Modifier.weight(1f)){Text(title,style=MaterialTheme.typography.titleLarge.copy(fontWeight=FontWeight.Bold),color=MaterialTheme.colorScheme.onPrimary);Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onPrimary.copy(alpha=0.8f))};Icon(Icons.AutoMirrored.Filled.ArrowForward,null,Modifier.size(20.dp),tint=MaterialTheme.colorScheme.onPrimary.copy(alpha=0.7f))}
    }
}

@Composable
private fun Feat(icon: ImageVector, label: String) {
    val c = MaterialTheme.colorScheme
    Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(icon,null,Modifier.size(22.dp),tint=c.onSurfaceVariant.copy(alpha=0.5f));Spacer(Modifier.height(4.dp));Text(label,style=MaterialTheme.typography.labelSmall,color=c.onSurfaceVariant.copy(alpha=0.5f))}
}
