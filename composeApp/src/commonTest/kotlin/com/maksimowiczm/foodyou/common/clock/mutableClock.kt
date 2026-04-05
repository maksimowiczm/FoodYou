package com.maksimowiczm.foodyou.common.clock

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

fun staticClock(instant: Instant = Clock.System.now()) =
    object : Clock {
        override fun now(): Instant = instant
    }

fun mutableClock(initial: Instant = Clock.System.now()) = MutableClock(initial)

class MutableClock(initial: Instant) : Clock {
    private var currentTime = initial

    override fun now(): Instant = currentTime

    fun advanceBy(duration: Duration) {
        currentTime += duration
    }
}
