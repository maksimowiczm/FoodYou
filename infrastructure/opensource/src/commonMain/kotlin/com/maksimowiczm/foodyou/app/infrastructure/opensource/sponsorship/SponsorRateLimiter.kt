package com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship

import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.YearMonth

@OptIn(ExperimentalTime::class)
internal class SponsorRateLimiter(
    private val dateProvider: DateProvider,
    private val timeWindow: Duration,
) {
    private val requests = mutableSetOf<RequestRecord>()

    fun recordRequest(yearMonth: YearMonth) {
        requests.add(RequestRecord(yearMonth, dateProvider.nowInstant()))
    }

    fun canMakeRequest(yearMonth: YearMonth): Boolean {
        val now = dateProvider.nowInstant()
        val windowStart = now - timeWindow

        // Remove requests outside the time window
        requests.removeAll { it.timestamp < windowStart }

        return requests.none { it.yearMonth == yearMonth }
    }
}

@OptIn(ExperimentalTime::class)
private class RequestRecord(val yearMonth: YearMonth, val timestamp: Instant)
