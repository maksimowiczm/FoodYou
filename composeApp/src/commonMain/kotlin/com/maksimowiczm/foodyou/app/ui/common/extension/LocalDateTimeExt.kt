package com.maksimowiczm.foodyou.app.ui.common.extension

import kotlin.time.Clock
import kotlin.time.Duration
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

operator fun LocalDateTime.plus(duration: Duration): LocalDateTime =
    toInstant(TimeZone.UTC).plus(duration).toLocalDateTime(TimeZone.UTC)

operator fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    toInstant(TimeZone.UTC).minus(duration).toLocalDateTime(TimeZone.UTC)
