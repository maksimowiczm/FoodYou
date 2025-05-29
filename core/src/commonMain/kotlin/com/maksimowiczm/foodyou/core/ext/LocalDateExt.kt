package com.maksimowiczm.foodyou.core.ext

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    Clock.System.now().toLocalDateTime(timeZone).date
