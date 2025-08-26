package com.maksimowiczm.foodyou.business.shared.application.infrastructure.date

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

interface DateProvider {

    fun now(): LocalDateTime

    /** Returns a [Flow] that emits the current date and time and updates it every second. */
    fun observeDateTime(): Flow<LocalDateTime>

    /** Returns a [Flow] that emits the current date and updates it at midnight. */
    fun observeDate(): Flow<LocalDate>

    /** Returns a [Flow] that emits the current time and updates it every minute. */
    fun observeTime(): Flow<LocalTime>
}
