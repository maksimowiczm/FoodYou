package com.maksimowiczm.foodyou.feature.system.data

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
            val tomorrowStart = now
                .plusDays(1)
            val delayMillis = ChronoUnit.MILLIS.between(now, tomorrowStart)

            delay(delayMillis)

            val newDate = getCurrentDate()
            if (newDate != currentDate) {
                currentDate = newDate
                emit(currentDate)
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = getCurrentDate()
    )

    private fun getCurrentDate(): LocalDate = LocalDate.now()
}
