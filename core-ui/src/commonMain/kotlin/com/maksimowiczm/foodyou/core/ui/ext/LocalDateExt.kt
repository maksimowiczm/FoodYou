package com.maksimowiczm.foodyou.core.ui.ext

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalDate

val LocalDate.Companion.Saver
    get() = Saver<LocalDate, Long>(
        save = { it.toEpochDays() },
        restore = { fromEpochDays(it) }
    )
