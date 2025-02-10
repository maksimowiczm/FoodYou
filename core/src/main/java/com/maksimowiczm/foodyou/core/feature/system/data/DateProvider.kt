package com.maksimowiczm.foodyou.core.feature.system.data

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

fun interface DateProvider {
    /**
     * Returns a [StateFlow] that emits the today's date.
     */
    fun observe(): StateFlow<LocalDate>
}
