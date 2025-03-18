package com.maksimowiczm.foodyou.ui.modifier

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

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

@Preview
@Composable
private fun AnimateRotationPreview() {
    Surface(
        modifier = Modifier.size(200.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .animateRotation()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxSize(.5f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .animateRotation(
                            initialValue = 360f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                tween(
                                    durationMillis = 950,
                                    easing = LinearEasing
                                )
                            )
                        )
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .fillMaxSize(.5f)
                )
            }
        }
    }
}
