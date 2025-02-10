package com.maksimowiczm.foodyou.core.feature.system.data

import android.util.Log
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

internal class DateProviderImpl(
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : DateProvider {
    override fun observe(): StateFlow<LocalDate> = flow {
        var currentDate = getCurrentDate()

        while (true) {
            val now = Clock.System.now()
            val midnight = Clock.System
                .todayIn(TimeZone.currentSystemDefault())
                .plus(1, DateTimeUnit.DAY)
                .atStartOfDayIn(TimeZone.currentSystemDefault())
            val delayMillis = (midnight - now).inWholeMilliseconds

            Log.d(TAG, "Current date: $currentDate")
            Log.d(TAG, "Delaying for $delayMillis ms")
            delay(delayMillis)
            Log.d(TAG, "Woke up")

            val newDate = getCurrentDate()
            Log.d(TAG, "New date: $newDate")

            if (newDate != currentDate) {
                currentDate = newDate

                Log.d(TAG, "Emitting new date: $currentDate")
                emit(currentDate)
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = getCurrentDate()
    )

    private fun getCurrentDate(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private companion object {
        private const val TAG = "DateProviderImpl"
    }
}
