package com.maksimowiczm.foodyou.common.infrastructure.network

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A simple rate limiter to limit the number of requests in a given time window.
 *
 * @param maxRequests The maximum number of requests allowed in the time window.
 * @param timeWindow The duration of the time window.
 */
class RateLimiter(
    private val clock: Clock,
    private val maxRequests: Int,
    private val timeWindow: Duration,
) {
    private val requests = mutableListOf<Instant>()
    private val mutex = Mutex()

    suspend fun canMakeRequest(): Boolean =
        mutex.withLock {
            val now = clock.now()
            val windowStart = now - timeWindow

            // Remove requests outside the time window
            requests.removeAll { it < windowStart }

            return requests.size < maxRequests
        }

    suspend fun recordRequest() = mutex.withLock { requests.add(clock.now()) }
}
