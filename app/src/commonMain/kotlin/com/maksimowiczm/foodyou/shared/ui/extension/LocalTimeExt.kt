package com.maksimowiczm.foodyou.shared.ui.extension

import kotlin.time.Duration
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime =
    LocalDateTime.now(timeZone).time

operator fun LocalTime.plus(duration: Duration): LocalTime {
    val dayNanos = 86400L * 1_000_000_000L
    val totalNanos = this.toNanosecondOfDay() + duration.inWholeNanoseconds
    return LocalTime.fromNanosecondOfDay(((totalNanos % dayNanos) + dayNanos) % dayNanos)
}
