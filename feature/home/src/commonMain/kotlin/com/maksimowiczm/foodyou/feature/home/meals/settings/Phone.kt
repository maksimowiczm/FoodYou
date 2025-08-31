package com.maksimowiczm.foodyou.feature.home.meals.settings

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun Phone(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.size(width = 120.dp, height = 100.dp),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        color = color,
    ) {
        content()
    }
}
