package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalDate

val LocalDate.Companion.Saver
    get() = Saver<LocalDate, Int>(
        save = { it.toEpochDays() },
        restore = { fromEpochDays(it) }
    )
