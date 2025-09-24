package com.maksimowiczm.foodyou.common.compose.extension

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalTime

val LocalTime.Companion.Saver
    get() =
        Saver<LocalTime, Int>(
            save = { it.toSecondOfDay() },
            restore = { LocalTime.fromSecondOfDay(it) },
        )
