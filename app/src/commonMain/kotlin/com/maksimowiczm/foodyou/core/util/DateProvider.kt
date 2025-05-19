package com.maksimowiczm.foodyou.core.util

import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

interface DateProvider {

    /**
     * Returns a [Flow] that emits the current date and time and updates it every second.
     */
    fun observeDateTime(): Flow<LocalDateTime>

    /**
     * Returns a [Flow] that emits the current date and updates it at midnight.
     */
    fun observeDate(): Flow<LocalDate>

    /**
     * Returns a [Flow] that emits the current time and updates it every minute.
     */
    fun observeMinutes(): Flow<LocalTime>
}

internal class DateProviderImpl : DateProvider {
    override fun observeDateTime(): Flow<LocalDateTime> = flow {
        while (true) {
            val dateTime = getCurrentDateTime()
            emit(dateTime)

            val millisUntilNextSecond = 1000L - dateTime.nanosecond / 1_000_000
            delay(millisUntilNextSecond)
        }
    }

    override fun observeDate(): Flow<LocalDate> = flow {
        var currentDate = getCurrentDateTime().date

        emit(currentDate)

        while (true) {
            val now = Clock.System.now()
            val midnight = Clock.System
                .todayIn(TimeZone.currentSystemDefault())
                .plus(1, DateTimeUnit.DAY)
                .atStartOfDayIn(TimeZone.currentSystemDefault())
            val delayMillis = (midnight - now).inWholeMilliseconds

            Logger.d(TAG) { "Current date: $currentDate" }
            Logger.d(TAG) { "Delaying for $delayMillis ms" }
            delay(delayMillis)
            Logger.d(TAG) { "Woke up" }

            val newDate = getCurrentDateTime().date
            Logger.d(TAG) { "New date: $newDate" }

            if (newDate != currentDate) {
                currentDate = newDate

                Logger.d(TAG) { "Emitting new date: $currentDate" }
                emit(currentDate)
            }
        }
    }

    override fun observeMinutes(): Flow<LocalTime> = flow {
        while (true) {
            val time = getCurrentDateTime().time
            emit(time)

            // Delay until minute changes
            val secondsUntilNextMinute = 60 - time.second
            delay(secondsUntilNextMinute * 1000L)
        }
    }

    private fun getCurrentDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private companion object {
        private const val TAG = "DateProviderImpl"
    }
}
