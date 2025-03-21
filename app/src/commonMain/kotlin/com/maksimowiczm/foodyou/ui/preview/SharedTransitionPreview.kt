package com.maksimowiczm.foodyou.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.maksimowiczm.foodyou.feature.diary.ui.LocalMealSharedTransitionScope
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope

/**
 * A preview composable that wraps the content in a [SharedTransitionLayout] and [AnimatedVisibility].
 *
 * @param block The content to preview.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPreview(
    block: @Composable SharedTransitionScope.(AnimatedVisibilityScope) -> Unit
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalHomeSharedTransitionScope provides this,
            LocalMealSharedTransitionScope provides this
        ) {
            AnimatedVisibility(true) {
                block(this@SharedTransitionLayout, this@AnimatedVisibility)
            }
        }
    }
}
