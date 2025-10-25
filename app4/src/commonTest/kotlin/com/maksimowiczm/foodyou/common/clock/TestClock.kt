package com.maksimowiczm.foodyou.common.clock

import kotlin.time.Clock
import kotlin.time.Instant

fun testClock(now: Instant): Clock =
    object : Clock {
        override fun now(): Instant {
            return now
        }
    }
