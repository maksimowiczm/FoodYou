package com.maksimowiczm.foodyou.shared.ui.ext

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

@OptIn(ExperimentalTime::class)
operator fun LocalDateTime.plus(duration: Duration): LocalDateTime =
    this.toInstant(TimeZone.UTC).plus(duration).toLocalDateTime(TimeZone.UTC)

@OptIn(ExperimentalTime::class)
operator fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    this.toInstant(TimeZone.UTC).minus(duration).toLocalDateTime(TimeZone.UTC)
