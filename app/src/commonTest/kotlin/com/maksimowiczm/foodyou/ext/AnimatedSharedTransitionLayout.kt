package com.maksimowiczm.foodyou.ext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable

@OptIn(ExperimentalSharedTransitionApi::class)
class AnimatedSharedTransitionLayout(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimatedSharedTransitionLayout(content: @Composable AnimatedSharedTransitionLayout.() -> Unit) {
    AnimatedVisibility(true) {
        SharedTransitionLayout {
            val animatedSharedTransitionLayout = AnimatedSharedTransitionLayout(
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@AnimatedVisibility
            )

            content(animatedSharedTransitionLayout)
        }
    }
}
