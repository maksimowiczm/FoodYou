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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal class AndroidTodayDateProvider(
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : TodayDateProvider {
    override fun observe(): StateFlow<LocalDate> = flow {
        var currentDate = getCurrentDate()

        while (true) {
            val now = LocalDateTime.now()
            val tomorrowStart = now.plusDays(1).toLocalDate().atStartOfDay()
            val delayMillis = ChronoUnit.MILLIS.between(now, tomorrowStart)

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

    private fun getCurrentDate(): LocalDate = LocalDate.now()

    private companion object {
        private const val TAG = "AndroidTodayDateProvider"
    }
}
