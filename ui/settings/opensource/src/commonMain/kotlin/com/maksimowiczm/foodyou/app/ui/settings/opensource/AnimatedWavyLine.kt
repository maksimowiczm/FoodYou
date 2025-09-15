package com.maksimowiczm.foodyou.app.ui.settings.opensource

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
internal fun AnimatedWavyLine(
    color: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
    frequency: Float = 0.05f,
    animationSpec: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation = tween(5_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by
        infiniteTransition.animateFloat(
            initialValue = 2 * PI.toFloat(),
            targetValue = 0f,
            animationSpec = animationSpec,
        )

    Canvas(modifier) {
        val centerY = size.height / 2
        val amplitude = size.height / 4

        val path =
            Path().apply {
                for (x in 0..size.width.toInt()) {
                    val y = centerY + amplitude * sin(frequency * x + phase)
                    if (x == 0) {
                        moveTo(x.toFloat(), y)
                    } else {
                        lineTo(x.toFloat(), y)
                    }
                }
            }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
        )
    }
}
