package com.maksimowiczm.foodyou.shared.domain.date

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalTime::class)
interface DateProvider {

    fun now(): LocalDateTime

    fun nowInstant(): Instant

    /** Returns a [Flow] that emits the current date and time and updates it every second. */
    fun observeDateTime(): Flow<LocalDateTime>

    /** Returns a [Flow] that emits the current date and updates it at midnight. */
    fun observeDate(): Flow<LocalDate>

    /** Returns a [Flow] that emits the current time and updates it every minute. */
    fun observeTime(): Flow<LocalTime>
}
