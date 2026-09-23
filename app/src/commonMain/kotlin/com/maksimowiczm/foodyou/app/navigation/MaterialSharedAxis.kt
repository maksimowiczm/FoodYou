/*
 * Copyright 2021 SOUP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Read You
 * https://github.com/Ashinch/ReadYou/blob/74d0c50da789ebd26bd49cd03ce73b000a54c569/app/src/main/java/me/ash/reader/ui/motion/MaterialSharedAxis.kt
 */
package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeIn
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeOut

/** [materialSharedAxisXIn] allows to switch a layout with shared X-axis enter transition. */
fun materialSharedAxisXIn(
    initialOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = DefaultDurationMillis,
): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        initialOffsetX = initialOffsetX,
    ) + crossfadeIn()

/** [materialSharedAxisXOut] allows to switch a layout with shared X-axis exit transition. */
fun materialSharedAxisXOut(
    targetOffsetX: (fullWidth: Int) -> Int,
    durationMillis: Int = DefaultDurationMillis,
): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        targetOffsetX = targetOffsetX,
    ) + crossfadeOut()
