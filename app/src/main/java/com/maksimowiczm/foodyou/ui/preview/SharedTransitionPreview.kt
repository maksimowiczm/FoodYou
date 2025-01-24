package com.maksimowiczm.foodyou.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

/**
 * A preview composable that wraps the content in a [SharedTransitionLayout] and [AnimatedVisibility].
 *
 * @param block The content to preview.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPreview(
    block: @Composable context(SharedTransitionScope, AnimatedVisibilityScope) AnimatedVisibilityScope.() -> Unit
) {
    FoodYouTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                block(this, this)
            }
        }
    }
}
