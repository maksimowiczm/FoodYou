package com.maksimowiczm.foodyou.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface DateProvider {
    /**
     * Returns a [Flow] that emits the current date and updates it at midnight.
     */
    fun observeDate(): Flow<LocalDate>

    /**
     * Returns a [Flow] that emits the current time and updates it every minute.
     */
    fun observeMinutes(): Flow<LocalTime>
}
