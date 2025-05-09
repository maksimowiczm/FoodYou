package com.maksimowiczm.foodyou.core.ui.home

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FoodYouHomeCard(
    modifier: Modifier = Modifier,
    colors: CardColors = FoodYouHomeCardDefaults.colors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = colors
    ) {
        content()
    }
}

@Composable
fun FoodYouHomeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
        ) {
            content()
        }
    }
}

object FoodYouHomeCardDefaults {
    @Composable
    fun colors(): CardColors = CardDefaults.elevatedCardColors()
}
