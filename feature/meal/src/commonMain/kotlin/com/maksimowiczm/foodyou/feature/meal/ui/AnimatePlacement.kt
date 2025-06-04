package com.maksimowiczm.foodyou.feature.meal.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun Modifier.animatePlacement(
    scope: CoroutineScope = rememberCoroutineScope(),
    animationSpec: AnimationSpec<IntOffset> = MaterialTheme.motionScheme.defaultSpatialSpec()
): Modifier {
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }

    return onPlaced {
        targetOffset = it.positionInParent().round()
    }.offset {
        val isInitialized = animatable != null
        val anim = animatable
            ?: Animatable(targetOffset, IntOffset.VectorConverter).also {
                animatable = it
            }

        if (!isInitialized) {
            scope.launch {
                anim.snapTo(targetOffset)
            }

            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        } else {
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, animationSpec)
                }
            }

            // Offset the child in the opposite direction to the targetOffset, and slowly catch
            // up to zero offset via an animation to achieve an overall animated movement.
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
    }
}
