package com.maksimowiczm.foodyou.ui.home

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
    colors: CardColors = FoodYouHomeCardDefaults.colors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = colors
    ) {
        content()
    }
}

object FoodYouHomeCardDefaults {
    @Composable
    fun colors(): CardColors = CardDefaults.elevatedCardColors()
}
