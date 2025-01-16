package com.maksimowiczm.foodyou.feature.diary.data

import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

fun interface TodayDateProvider {
    /**
     * Returns a [StateFlow] that emits the today's date.
     */
    fun observe(): StateFlow<LocalDate>
}
