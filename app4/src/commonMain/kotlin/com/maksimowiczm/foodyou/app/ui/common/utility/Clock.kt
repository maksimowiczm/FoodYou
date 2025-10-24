package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Clock

val LocalClock = staticCompositionLocalOf<Clock> { Clock.System }

@Composable
fun ClockProvider(clock: Clock, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalClock provides clock) { content() }
}
