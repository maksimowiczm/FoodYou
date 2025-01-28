package com.maksimowiczm.foodyou.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

/**
 * A preview composable that wraps the content in a [SharedTransitionLayout] and [AnimatedVisibility].
 *
 * @param block The content to preview.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPreview(
    block: @Composable (SharedTransitionScope, AnimatedVisibilityScope) -> Unit
) {
    FoodYouTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout
                ) {
                    block(this@SharedTransitionLayout, this@AnimatedVisibility)
                }
            }
        }
    }
}
