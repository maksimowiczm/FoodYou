package com.maksimowiczm.foodyou.common.clock

import kotlin.time.Clock
import kotlin.time.Instant

fun testClock(now: Instant = Clock.System.now()): Clock =
    object : Clock {
        override fun now(): Instant = now
    }
