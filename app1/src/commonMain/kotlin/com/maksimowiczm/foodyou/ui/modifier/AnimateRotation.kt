package com.maksimowiczm.foodyou.ui.modifier

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.animateRotation(
    initialValue: Float = 0f,
    targetValue: Float = 360f,
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    ),
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition(),
    label: String = "RotationAnimation"
): Modifier {
    val rotation by infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = label
    )

    return graphicsLayer {
        rotationZ = rotation
    }
}
