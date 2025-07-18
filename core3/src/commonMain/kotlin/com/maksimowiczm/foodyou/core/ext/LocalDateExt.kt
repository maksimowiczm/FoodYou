package com.maksimowiczm.foodyou.core.ext

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
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
    LocalDateTime.now(timeZone).date

fun LocalDate.plus(duration: Duration): LocalDate =
    this.plus(duration.inWholeDays, DateTimeUnit.DAY)

operator fun LocalDate.minus(duration: Duration): LocalDate =
    minus(duration.inWholeDays, DateTimeUnit.DAY)
