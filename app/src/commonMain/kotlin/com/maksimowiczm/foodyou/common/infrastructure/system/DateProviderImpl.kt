package com.maksimowiczm.foodyou.common.infrastructure.system

import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

internal class DateProviderImpl : DateProvider {
    override fun nowInstant(): Instant = Clock.System.now()

    override fun observeInstant(interval: Duration): Flow<Instant> = flow {
        while (true) {
            emit(nowInstant())
            delay(interval)
        }
    }

    override fun observeDate(timeZone: TimeZone): Flow<LocalDate> = flow {
        while (true) {
            val currentDate = nowInstant().toLocalDateTime(timeZone).date
            emit(currentDate)

            val now = Clock.System.now()
            val midnight =
                Clock.System.todayIn(timeZone).plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
            val delayMillis = (midnight - now).inWholeMilliseconds

            delay(delayMillis)
        }
    }
}
