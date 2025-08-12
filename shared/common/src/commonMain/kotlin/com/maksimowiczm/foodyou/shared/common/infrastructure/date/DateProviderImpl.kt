package com.maksimowiczm.foodyou.shared.common.infrastructure.date

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@OptIn(ExperimentalTime::class)
internal class DateProviderImpl : DateProvider {

    override fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    override fun observeDateTime(): Flow<LocalDateTime> = flow {
        while (true) {
            val dateTime = now()
            emit(dateTime)

            val millisUntilNextSecond = 1000L - dateTime.nanosecond / 1_000_000
            delay(millisUntilNextSecond)
        }
    }

    override fun observeDate(): Flow<LocalDate> = flow {
        var currentDate = now().date

        emit(currentDate)

        while (true) {
            val now = Clock.System.now()
            val midnight =
                Clock.System.todayIn(TimeZone.currentSystemDefault())
                    .plus(1, DateTimeUnit.DAY)
                    .atStartOfDayIn(TimeZone.currentSystemDefault())
            val delayMillis = (midnight - now).inWholeMilliseconds

            Logger.d(TAG) { "Current date: $currentDate" }
            Logger.d(TAG) { "Delaying for $delayMillis ms" }
            delay(delayMillis)
            Logger.d(TAG) { "Woke up" }

            val newDate = now().date
            Logger.d(TAG) { "New date: $newDate" }

            if (newDate != currentDate) {
                currentDate = newDate

                Logger.d(TAG) { "Emitting new date: $currentDate" }
                emit(currentDate)
            }
        }
    }

    override fun observeTime(): Flow<LocalTime> = flow {
        while (true) {
            val time = now().time
            emit(time)

            // Delay until minute changes
            val secondsUntilNextMinute = 60 - time.second
            delay(secondsUntilNextMinute * 1000L)
        }
    }

    private companion object {
        private const val TAG = "DateProviderImpl"
    }
}
