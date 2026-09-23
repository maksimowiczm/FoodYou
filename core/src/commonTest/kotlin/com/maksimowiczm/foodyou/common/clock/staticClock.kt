package com.maksimowiczm.foodyou.common.clock

import kotlin.time.Clock
import kotlin.time.Instant

fun staticClock(instant: Instant = Clock.System.now()) =
    object : Clock {
        override fun now(): Instant = instant
    }
