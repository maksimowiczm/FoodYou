package com.maksimowiczm.foodyou.common.clock.domain

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun Clock.observe(interval: Duration = 1.seconds) = flow {
    while (true) {
        emit(now())
        delay(interval)
    }
}
