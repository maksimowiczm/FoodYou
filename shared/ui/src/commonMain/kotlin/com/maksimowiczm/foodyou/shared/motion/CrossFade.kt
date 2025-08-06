package com.maksimowiczm.foodyou.shared.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

fun crossfadeIn(durationMillis: Int = DefaultDurationMillis): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = durationMillis.ForIncoming,
                delayMillis = durationMillis.ForOutgoing,
                easing = LinearOutSlowInEasing,
            )
    )

fun crossfadeOut(durationMillis: Int = DefaultDurationMillis): ExitTransition =
    fadeOut(
        animationSpec =
            tween(
                durationMillis = durationMillis.ForOutgoing,
                delayMillis = 0,
                easing = FastOutLinearInEasing,
            )
    )
