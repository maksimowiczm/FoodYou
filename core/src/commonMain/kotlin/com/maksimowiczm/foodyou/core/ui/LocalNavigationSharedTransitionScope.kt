package com.maksimowiczm.foodyou.core.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalNavigationSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
