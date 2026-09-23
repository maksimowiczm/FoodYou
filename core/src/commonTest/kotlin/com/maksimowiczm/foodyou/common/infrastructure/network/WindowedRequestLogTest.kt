package com.maksimowiczm.foodyou.common.infrastructure.network

import com.maksimowiczm.foodyou.common.clock.testClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest

class WindowedRequestLogTest {
    @Test
    fun tryAcquire_allows_requests_until_limit_is_reached() = runTest {
        val log = WindowedRequestLog(maxRequests = 3, timeWindow = 10.seconds, clock = testClock())

        repeat(3) { assertNull(log.tryAcquire()) }

        assertEquals(10.seconds, log.tryAcquire())
    }

    @Test
    fun tryAcquire_returns_time_until_oldest_request_leaves_window() = runTest {
        val log = WindowedRequestLog(maxRequests = 2, timeWindow = 10.seconds, clock = testClock())

        assertNull(log.tryAcquire())
        advanceTimeBy(4.seconds)
        assertNull(log.tryAcquire())

        advanceTimeBy(1.seconds)

        assertEquals(5.seconds, log.tryAcquire())
    }

    @Test
    fun tryAcquire_allows_new_request_after_old_requests_expire() = runTest {
        val log = WindowedRequestLog(maxRequests = 2, timeWindow = 10.seconds, clock = testClock())

        assertNull(log.tryAcquire())
        advanceTimeBy(1.seconds)
        assertNull(log.tryAcquire())

        advanceTimeBy(11.seconds)

        assertNull(log.tryAcquire())
    }

    @Test
    fun tryAcquire_expires_request_exactly_at_window_end() = runTest {
        val log = WindowedRequestLog(maxRequests = 1, timeWindow = 10.seconds, clock = testClock())

        assertNull(log.tryAcquire())

        advanceTimeBy(10.seconds)

        assertNull(log.tryAcquire())
    }
}
