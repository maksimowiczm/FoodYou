package com.maksimowiczm.foodyou.core.ext

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

@OptIn(ExperimentalTime::class)
fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    Clock.System.now().toLocalDateTime(timeZone).date

fun LocalDate.plus(duration: Duration): LocalDate {
    require(duration.isPositive()) { "Duration must be positive" }
    require(duration.inWholeDays >= 0) { "Duration must be non-negative whole days" }
    return this.plus(DatePeriod(days = duration.inWholeDays.toInt()))
}

fun LocalDate.minus(duration: Duration): LocalDate {
    require(duration.isPositive()) { "Duration must be positive" }
    require(duration.inWholeDays >= 0) { "Duration must be non-negative whole days" }
    return this.minus(DatePeriod(days = duration.inWholeDays.toInt()))
}
