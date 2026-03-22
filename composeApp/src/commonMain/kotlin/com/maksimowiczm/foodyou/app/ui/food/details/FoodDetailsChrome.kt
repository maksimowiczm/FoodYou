package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

internal data class FoodDetailsChrome(
    val statusBarProtection: Float,
    val iconButtonContainerColor: Color,
)

@Composable
internal fun rememberFoodDetailsChrome(lazyListState: LazyListState): FoodDetailsChrome {
    val animatedIsScrolled =
        animateFloatAsState(
            targetValue = if (lazyListState.canScrollBackward) 1f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val animatedIconButtonColor =
        animateColorAsState(
            targetValue =
                if (lazyListState.canScrollBackward) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surface
        )

    return FoodDetailsChrome(
        statusBarProtection = animatedIsScrolled.value,
        iconButtonContainerColor = animatedIconButtonColor.value,
    )
}
