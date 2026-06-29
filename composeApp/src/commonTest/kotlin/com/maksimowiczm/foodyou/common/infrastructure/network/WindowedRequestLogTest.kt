package com.maksimowiczm.foodyou.common.infrastructure.network

import com.maksimowiczm.foodyou.common.clock.mutableClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest

class WindowedRequestLogTest {
    @Test
    fun tryAcquire_allows_requests_until_limit_is_reached() = runTest {
        val clock = mutableClock()
        val log = WindowedRequestLog(maxRequests = 3, timeWindow = 10.seconds, clock = clock)

        repeat(3) { assertNull(log.tryAcquire()) }

        assertEquals(10.seconds, log.tryAcquire())
    }

    @Test
    fun tryAcquire_returns_time_until_oldest_request_leaves_window() = runTest {
        val clock = mutableClock()
        val log = WindowedRequestLog(maxRequests = 2, timeWindow = 10.seconds, clock = clock)

        assertNull(log.tryAcquire())
        clock.advanceBy(4.seconds)
        assertNull(log.tryAcquire())

        clock.advanceBy(1.seconds)

        assertEquals(5.seconds, log.tryAcquire())
    }

    @Test
    fun tryAcquire_allows_new_request_after_old_requests_expire() = runTest {
        val clock = mutableClock()
        val log = WindowedRequestLog(maxRequests = 2, timeWindow = 10.seconds, clock = clock)

        assertNull(log.tryAcquire())
        clock.advanceBy(1.seconds)
        assertNull(log.tryAcquire())

        clock.advanceBy(11.seconds)

        assertNull(log.tryAcquire())
    }

    @Test
    fun tryAcquire_treats_request_at_window_start_as_in_window() = runTest {
        val clock = mutableClock()
        val log = WindowedRequestLog(maxRequests = 1, timeWindow = 10.seconds, clock = clock)

        assertNull(log.tryAcquire())
        clock.advanceBy(10.seconds)

        assertEquals(0.seconds, log.tryAcquire())
    }
}
