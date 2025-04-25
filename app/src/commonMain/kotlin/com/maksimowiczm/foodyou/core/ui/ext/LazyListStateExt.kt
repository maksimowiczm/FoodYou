package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.flow.collectLatest

/**
 * Calculates the alpha value of the first visible item in a [LazyListState] based on its scroll
 * offset. The alpha value is interpolated between 0 and 1 based on the scroll offset of the first
 * visible item.
 *
 * @return The alpha value of the first visible item, which is a float between 0 and 1.
 */
@Composable
fun LazyListState.firstVisibleItemAlpha(firstItemHeight: Int): Float {
    var headlineAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(this, firstItemHeight) {
        snapshotFlow { firstVisibleItemScrollOffset }.collectLatest {
            headlineAlpha = if (firstVisibleItemIndex != 0) {
                1f
            } else if (firstItemHeight == 0) {
                0f
            } else {
                lerp(0f, 1f, it / firstItemHeight.toFloat())
            }
        }
    }

    return headlineAlpha
}
