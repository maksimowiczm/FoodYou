package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalTime

val LocalTime.Companion.Saver
    get() = Saver<LocalTime, Int>(
        save = { it.toSecondOfDay() },
        restore = { LocalTime.fromSecondOfDay(it) }
    )
