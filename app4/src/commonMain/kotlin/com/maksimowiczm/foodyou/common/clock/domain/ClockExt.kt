package com.maksimowiczm.foodyou.common.clock.domain

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Observes the current time, emitting updates at the given [interval].
 *
 * @param interval The interval at which to emit the current time.
 */
fun Clock.observe(interval: Duration = 1.seconds) = flow {
    while (true) {
        emit(now())
        delay(interval)
    }
}

/**
 * Observes the current date, emitting updates only when the date changes.
 *
 * @param interval The interval at which to check for date changes.
 * @param timeZone The time zone to use for date conversion.
 */
fun Clock.observeDate(
    interval: Duration = 1.seconds,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) = observe(interval).map { it.toLocalDateTime(timeZone).date }.distinctUntilChanged()
