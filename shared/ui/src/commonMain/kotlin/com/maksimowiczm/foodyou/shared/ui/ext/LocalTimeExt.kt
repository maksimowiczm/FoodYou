package com.maksimowiczm.foodyou.shared.ui.ext

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

val LocalTime.Companion.Saver
    get() =
        Saver<LocalTime, Int>(
            save = { it.toSecondOfDay() },
            restore = { LocalTime.fromSecondOfDay(it) },
        )

fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime =
    LocalDateTime.now(timeZone).time
