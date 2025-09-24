package com.maksimowiczm.foodyou.common.extension

import kotlin.time.Duration
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    LocalDateTime.now(timeZone).date

operator fun LocalDate.plus(duration: Duration): LocalDate =
    plus(duration.inWholeDays, DateTimeUnit.DAY)

operator fun LocalDate.minus(duration: Duration): LocalDate =
    minus(duration.inWholeDays, DateTimeUnit.DAY)
