package com.maksimowiczm.foodyou.common.compose.extension

import androidx.compose.runtime.saveable.Saver
import kotlinx.datetime.LocalDate

val LocalDate.Companion.Saver
    get() = Saver<LocalDate, Long>(save = { it.toEpochDays() }, restore = { fromEpochDays(it) })
