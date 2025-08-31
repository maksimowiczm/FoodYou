package com.maksimowiczm.foodyou.feature.home.shared

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun FoodYouHomeCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable () -> Unit,
) {
    Surface(modifier = modifier, color = color, shape = MaterialTheme.shapes.medium) { content() }
}

@Composable
internal fun FoodYouHomeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = FoodYouHomeCardDefaults.color,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Surface(modifier = modifier, color = color, shape = MaterialTheme.shapes.medium) {
        Box(modifier = Modifier.combinedClickable(onLongClick = onLongClick, onClick = onClick)) {
            content()
        }
    }
}

internal object FoodYouHomeCardDefaults {

    val color: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerLow
}
