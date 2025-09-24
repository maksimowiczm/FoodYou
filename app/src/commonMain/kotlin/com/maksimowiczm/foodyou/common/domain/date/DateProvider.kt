package com.maksimowiczm.foodyou.common.domain.date

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface DateProvider {

    fun nowInstant(): Instant

    fun now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
        nowInstant().toLocalDateTime(timeZone)

    fun observeInstant(interval: Duration = 1.minutes): Flow<Instant>

    fun observe(
        interval: Duration = 1.minutes,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Flow<LocalDateTime> = observeInstant(interval).map { it.toLocalDateTime(timeZone) }

    /** Returns a [Flow] that emits the current date and updates it at midnight. */
    fun observeDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): Flow<LocalDate>

    fun observeTime(
        interval: Duration = 1.minutes,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Flow<LocalTime> = observe(interval, timeZone).map { it.time }
}
