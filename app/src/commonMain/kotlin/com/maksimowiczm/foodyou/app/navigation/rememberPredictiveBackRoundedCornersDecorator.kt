package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@Composable
fun <T : Any> rememberPredictiveBackRoundedCornersDecorator(
    maxRadius: Dp = 32.dp
): NavEntryDecorator<T> {
    val motion = MaterialTheme.motionScheme
    return remember(motion) {
        NavEntryDecorator { entry ->
            val radius =
                LocalNavAnimatedContentScope.current.transition.animateFloat(
                    transitionSpec = { motion.defaultSpatialSpec() },
                    label = "corners",
                ) { state ->
                    when (state) {
                        EnterExitState.PostExit -> 1f
                        else -> 0f
                    }
                }

            Box(
                Modifier.graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(radius.value * maxRadius)
                }
            ) {
                entry.Content()
            }
        }
    }
}
