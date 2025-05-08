package com.maksimowiczm.foodyou.feature.meal.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.maksimowiczm.foodyou.core.ui.motion.AnimationConstantsExt

object MealScreenSharedTransition {
    const val FAB_CONTAINER = "meal_fab_container"
    const val FAB_CONTENT = "meal_fab_content"

    val fabContainerEnterTransition: EnterTransition = fadeIn()
    val fabContainerExitTransition: ExitTransition = fadeOut(
        tween(
            durationMillis = AnimationConstantsExt.EmphasizedDurationMillis,
            easing = FastOutLinearInEasing
        )
    )
    val fabContentEnterTransition: EnterTransition = fadeIn(tween())
    val fabContentExitTransition: ExitTransition = fadeOut(tween(150))

    val smallFabEnterTransition: EnterTransition = fadeIn(
        tween(
            delayMillis = 300
        )
    )

    val screenContainerEnterTransition: EnterTransition = fadeIn(
        tween(
            delayMillis = 200
        )
    )
    val screenContainerExitTransition: ExitTransition = fadeOut()
    val screenContentEnterTransition: EnterTransition = fadeIn(
        tween(
            delayMillis = 150
        )
    )
    val screenContentExitTransition: ExitTransition = fadeOut(tween(100))
}
