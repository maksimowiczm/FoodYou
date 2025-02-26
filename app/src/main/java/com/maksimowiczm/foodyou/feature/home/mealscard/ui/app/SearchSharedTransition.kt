package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.maksimowiczm.foodyou.ui.motion.AnimationConstantsExt.EmphasizedDurationMillis

object SearchSharedTransition {
    const val CONTAINER = "meal_search_container"
    const val CONTENT = "meal_search_content"

    val fabContainerEnterTransition: EnterTransition = fadeIn()
    val fabContainerExitTransition: ExitTransition = fadeOut(
        tween(
            durationMillis = EmphasizedDurationMillis,
            easing = FastOutLinearInEasing
        )
    )
    val fabContentEnterTransition: EnterTransition = fadeIn(tween())
    val fabContentExitTransition: ExitTransition = fadeOut(tween(150))

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
