package com.maksimowiczm.foodyou.feature.diary

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import com.maksimowiczm.foodyou.ui.motion.AnimationConstantsExt
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn

object SearchSharedTransitionSpecs {
    private fun <T> exitSpec() = tween<T>(50)

    val fabEnterTransition = crossfadeIn()

    val fabContentEnterTransition = crossfadeIn()

    val fabExitTransition = fadeOut(
        tween(
            durationMillis = AnimationConstantsExt.EmphasizedDurationMillis,
            easing = LinearOutSlowInEasing
        )
    )

    val fabContentExitTransition = fadeOut(exitSpec())
}
