package com.maksimowiczm.foodyou.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun ProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .size(
                width = 100.dp,
                height = 4.dp
            )
            .clip(MaterialTheme.shapes.small)
    ) {
        @Suppress("NAME_SHADOWING")
        val progress = progress()

        val progressWidth = (size.width * progress).coerceAtMost(size.width)
        val restWidth = size.width - progressWidth

        drawRect(
            color = progressColor,
            size = size.copy(width = progressWidth)
        )
        drawRect(
            color = trackColor,
            topLeft = Offset(x = progressWidth, y = 0f),
            size = size.copy(width = restWidth)
        )
    }
}

@PreviewDynamicColors
@Composable
fun ProgressIndicatorPreview() {
    FoodYouTheme {
        ProgressIndicator(progress = { 0.5f })
    }
}
