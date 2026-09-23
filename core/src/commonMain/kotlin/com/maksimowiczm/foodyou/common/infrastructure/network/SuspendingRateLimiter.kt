package com.maksimowiczm.foodyou.common.infrastructure.network

import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.coroutines.delay

/**
 * A [RateLimiter] that suspends until a request slot becomes available or [timeout] is reached.
 *
 * Rather than immediately rejecting requests, this limiter waits precisely until the oldest request
 * leaves the time window, minimizing unnecessary delays.
 *
 * @param log The [WindowedRequestLog] used to track and enforce the rate limit.
 * @param timeout The maximum duration to wait for a slot before giving up.
 * @param minWaitTime The minimum duration to wait between consecutive requests.
 */
class SuspendingRateLimiter(
    private val log: WindowedRequestLog,
    private val timeout: Duration,
    private val minWaitTime: Duration = Duration.ZERO,
) : RateLimiter {
    private var lastRequestTime: Instant? = null
    private val clock = log.clock

    /**
     * Suspends until a request slot is available or [timeout] is exceeded.
     *
     * Enforces both the rate limit from [log] and the [minWaitTime] between consecutive requests.
     *
     * @return `true` if a slot was acquired, `false` if the timeout was reached.
     */
    override suspend fun acquire(): Boolean {
        val deadline = clock.now() + timeout

        while (true) {
            val now = clock.now()
            val lastRequest = lastRequestTime

            // Check if minimum wait time has passed since the last request
            val minWaitDelay =
                if (lastRequest != null) {
                    val elapsed = now - lastRequest
                    if (elapsed < minWaitTime) {
                        minWaitTime - elapsed
                    } else {
                        Duration.ZERO
                    }
                } else {
                    Duration.ZERO
                }

            // Check if a slot is available from the rate limit log
            val logDelay = log.tryAcquire()

            // Determine the total delay needed
            val totalDelay =
                if (logDelay == null) {
                    // Slot is available, but we might still need to wait for minimum time
                    if (minWaitDelay > Duration.ZERO) {
                        minWaitDelay
                    } else {
                        // Both conditions satisfied, acquire the slot
                        lastRequestTime = now
                        return true
                    }
                } else {
                    // No slot available, wait for the longer of the two delays
                    maxOf(minWaitDelay, logDelay)
                }

            // Clamp delay to not exceed the deadline
            val cappedDelay = minOf(totalDelay, deadline - clock.now())
            if (cappedDelay <= Duration.ZERO) return false

            delay(cappedDelay)
        }
    }
}
