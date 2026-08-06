package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

object Crossfade {
    const val PROGRESS_THRESHOLD = .35f

    private val Int.ForOutgoing: Int
        get() = (this * PROGRESS_THRESHOLD).toInt()

    private val Int.ForIncoming: Int
        get() = this - this.ForOutgoing

    fun crossfadeIn(
        durationMillis: Int = DefaultDurationMillis,
        easing: Easing = LinearOutSlowInEasing,
    ): EnterTransition =
        fadeIn(
            animationSpec =
                tween(
                    durationMillis = durationMillis.ForIncoming,
                    delayMillis = durationMillis.ForOutgoing,
                    easing = easing,
                )
        )

    fun crossfadeOut(
        durationMillis: Int = DefaultDurationMillis,
        easing: Easing = FastOutLinearInEasing,
    ): ExitTransition =
        fadeOut(
            animationSpec =
                tween(durationMillis = durationMillis.ForOutgoing, delayMillis = 0, easing = easing)
        )

    fun crossfade(): ContentTransform = crossfadeIn() togetherWith crossfadeOut()
}
