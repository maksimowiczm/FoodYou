package com.maksimowiczm.foodyou.feature.system.data

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface DateProvider {
    /**
     * Returns a [StateFlow] that emits the current date at midnight.
     */
    fun observeDate(): StateFlow<LocalDate>

    /**
     * Returns a [StateFlow] that emits the current time every minute.
     */
    fun observeMinutes(): StateFlow<LocalTime>
}
