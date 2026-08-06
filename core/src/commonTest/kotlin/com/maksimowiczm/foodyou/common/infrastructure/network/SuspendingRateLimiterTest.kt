package com.maksimowiczm.foodyou.common.infrastructure.network

import com.maksimowiczm.foodyou.common.clock.testClock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class SuspendingRateLimiterTest {
    @Test
    fun acquire_returns_true_immediately_when_slot_is_available() = runTest {
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 1,
                        timeWindow = 1.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
            )

        assertTrue(limiter.acquire())
    }

    @Test
    fun acquire_waits_until_the_window_opens_then_returns_true() = runTest {
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 1,
                        timeWindow = 1.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
            )

        assertTrue(limiter.acquire())
        val result = async { limiter.acquire() }

        runCurrent()
        assertFalse(result.isCompleted)

        advanceTimeBy(1.seconds + 1.milliseconds)
        runCurrent()

        assertTrue(result.isCompleted, "Expected acquire to complete, but it didn't")
        assertTrue(result.await(), "Expected acquire to succeed, but it failed")
    }

    @Test
    fun acquire_returns_false_when_timeout_expires_before_a_slot_opens() = runTest {
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 1,
                        timeWindow = 1.seconds,
                        clock = testClock(),
                    ),
                timeout = 500.milliseconds,
            )

        assertTrue(limiter.acquire())
        val result = async { limiter.acquire() }

        runCurrent()
        assertFalse(result.isCompleted)

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertFalse(result.await())
    }

    @Test
    fun acquire_respects_minWaitTime_between_consecutive_requests() = runTest {
        val minWaitTime = 500.milliseconds
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 100,
                        timeWindow = 10.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
                minWaitTime = minWaitTime,
            )

        // First acquire should succeed immediately
        assertTrue(limiter.acquire())

        // Second acquire should wait for minWaitTime
        val result = async { limiter.acquire() }

        runCurrent()
        assertFalse(result.isCompleted)

        // Advance time by less than minWaitTime
        advanceTimeBy(minWaitTime - 100.milliseconds)
        runCurrent()
        assertFalse(result.isCompleted)

        // Advance time to complete the minWaitTime
        advanceTimeBy(100.milliseconds)
        runCurrent()

        assertTrue(result.await())
    }

    @Test
    fun acquire_respects_minWaitTime_over_rate_limit() = runTest {
        val minWaitTime = 1.seconds
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 100,
                        timeWindow = 10.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
                minWaitTime = minWaitTime,
            )

        // First acquire should succeed immediately
        assertTrue(limiter.acquire())

        // Second acquire should wait for minWaitTime (not available from log, but needed for
        // minWaitTime)
        val result = async { limiter.acquire() }

        runCurrent()
        assertFalse(result.isCompleted)

        // Advance time just short of minWaitTime
        advanceTimeBy(minWaitTime - 100.milliseconds)
        runCurrent()
        assertFalse(result.isCompleted)

        // Advance time to complete the minWaitTime
        advanceTimeBy(100.milliseconds)
        runCurrent()

        assertTrue(result.await())
    }

    @Test
    fun acquire_times_out_when_minWaitTime_exceeds_timeout() = runTest {
        val minWaitTime = 1.seconds
        val timeout = 500.milliseconds
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 100,
                        timeWindow = 10.seconds,
                        clock = testClock(),
                    ),
                timeout = timeout,
                minWaitTime = minWaitTime,
            )

        // First acquire should succeed immediately
        assertTrue(limiter.acquire())

        // Second acquire should time out because minWaitTime > timeout
        val result = async { limiter.acquire() }

        runCurrent()
        assertFalse(result.isCompleted)

        // Advance time by the timeout
        advanceTimeBy(timeout)
        runCurrent()

        assertFalse(result.await())
    }

    @Test
    fun acquire_allows_multiple_sequential_requests_respecting_minWaitTime() = runTest {
        val minWaitTime = 100.milliseconds
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 100,
                        timeWindow = 10.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
                minWaitTime = minWaitTime,
            )

        // First request
        assertTrue(limiter.acquire())

        // Second request with proper delay
        advanceTimeBy(minWaitTime)
        assertTrue(limiter.acquire())

        // Third request with proper delay
        advanceTimeBy(minWaitTime)
        assertTrue(limiter.acquire())
    }

    @Test
    fun acquire_with_zero_minWaitTime_does_not_enforce_delay() = runTest {
        val limiter =
            SuspendingRateLimiter(
                log =
                    WindowedRequestLog(
                        maxRequests = 100,
                        timeWindow = 10.seconds,
                        clock = testClock(),
                    ),
                timeout = 5.seconds,
                minWaitTime = Duration.ZERO,
            )

        // All requests should succeed immediately
        assertTrue(limiter.acquire())
        assertTrue(limiter.acquire())
        assertTrue(limiter.acquire())
    }
}
