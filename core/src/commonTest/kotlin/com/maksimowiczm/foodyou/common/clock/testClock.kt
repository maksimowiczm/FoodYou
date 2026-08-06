package com.maksimowiczm.foodyou.common.clock

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.test.TestScope

fun TestScope.testClock() =
    object : Clock {
        override fun now() = Instant.fromEpochMilliseconds(testScheduler.currentTime)
    }
