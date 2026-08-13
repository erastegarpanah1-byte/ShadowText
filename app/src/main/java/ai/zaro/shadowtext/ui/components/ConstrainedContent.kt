package ai.zaro.shadowtext.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Responsive container that centers content and caps max width.
 * On narrow phones (360dp): fills with innerPadding
 * On wide/tablet screens: centers with maxWidth cap
 */
@Composable
fun ConstrainedContent(
    modifier: Modifier = Modifier,
    maxWidth: Int = 480,
    innerPadding: Int = 24,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Use full width on small screens, cap on large
        val capWidthPx = maxWidth.dp
        Box(
            modifier = Modifier
                .widthIn(max = capWidthPx)
                .fillMaxSize()
                .padding(horizontal = innerPadding.dp),
            content = content
        )
    }
}

/**
 * Responsive column with constrained width.
 * Stacks vertically, centers horizontally, caps max width.
 */
@Composable
fun ConstrainedColumn(
    modifier: Modifier = Modifier,
    maxWidth: Int = 480,
    innerPadding: Int = 24,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    ConstrainedContent(modifier = modifier, maxWidth = maxWidth, innerPadding = innerPadding) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
