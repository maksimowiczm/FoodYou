package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope

/**
 * Composition local that provides the [SharedTransitionScope] for the meal screen.
 *
 * It must be a separate scope to [LocalHomeSharedTransitionScope] because otherwise it will "leak"
 * composables that are part of the meal screen. e.g. add food FAB will be visible in the home
 * screen if the shared transition scope is shared.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalMealSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
