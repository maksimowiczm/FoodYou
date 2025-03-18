package com.maksimowiczm.foodyou.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Immutable
data class MultiColorProgressIndicatorItem(val progress: Float, val color: Color)

@Composable
fun MultiColorProgressIndicator(
    items: List<MultiColorProgressIndicatorItem>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    gapSize: Dp = 2.dp
) {
    val gaps = items.map {
        animateDpAsState(targetValue = if (it.progress > 0.01) gapSize else 0.dp).value
    }

    Canvas(
        modifier = modifier.size(
            width = MultiColorProgressIndicatorDefaults.width,
            height = MultiColorProgressIndicatorDefaults.height
        )
    ) {
        var start = 0f

        items.zip(gaps).forEach { (item, gap) ->
            drawRect(
                color = item.color,
                topLeft = Offset(x = start, y = 0f),
                size = Size(width = size.width * item.progress, height = size.height)
            )

            start += size.width * item.progress

            if (item.progress > 0) {
                start += gap.toPx()
            }
        }

        drawRect(
            color = trackColor,
            topLeft = Offset(x = start, y = 0f),
            size = Size(width = size.width - start, height = size.height)
        )
    }
}

object MultiColorProgressIndicatorDefaults {
    val width = 240.dp
    val height = 4.dp
}

@Preview
@Composable
private fun MultiColorProgressIndicatorPreview() {
    MaterialTheme {
        MultiColorProgressIndicator(
            items = listOf(
                MultiColorProgressIndicatorItem(0.15f, Color.Red),
                MultiColorProgressIndicatorItem(0.1f, Color.Green),
                MultiColorProgressIndicatorItem(0.3f, Color.Blue)
            )
        )
    }
}
