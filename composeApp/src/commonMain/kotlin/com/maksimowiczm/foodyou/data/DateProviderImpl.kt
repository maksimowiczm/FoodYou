package com.maksimowiczm.foodyou.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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

internal class DateProviderImpl(
    private val coroutineScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )
) : DateProvider {
    override fun observeDate(): StateFlow<LocalDate> = flow {
        var currentDate = getCurrentDateTime().date

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
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = getCurrentDateTime().date
    )

    override fun observeMinutes(): StateFlow<LocalTime> = flow {
        while (true) {
            val time = getCurrentDateTime().time
            emit(time)

            // Delay until minute changes
            val secondsUntilNextMinute = 60 - time.second
            delay(secondsUntilNextMinute * 1000L)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = getCurrentDateTime().time
    )

    private fun getCurrentDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private companion object {
        private const val TAG = "DateProviderImpl"
    }
}
