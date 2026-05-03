package com.maksimowiczm.foodyou.common.infrastructure.network

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A sliding window log of request timestamps used to enforce rate limits.
 *
 * Tracks when requests were made and determines whether a new request fits within the allowed
 * [maxRequests] per [timeWindow].
 *
 * @param clock The clock used to get the current time.
 * @param maxRequests The maximum number of requests allowed within [timeWindow].
 * @param timeWindow The duration of the sliding time window.
 */
class WindowedRequestLog(
    private val clock: Clock,
    private val maxRequests: Int,
    private val timeWindow: Duration,
) {
    private val requests = mutableListOf<Instant>()
    private val mutex = Mutex()

    /**
     * Attempts to acquire a slot for a new request.
     *
     * @return `null` if the slot was acquired, or the [Duration] until the next slot opens if the
     *   limit has been reached.
     */
    suspend fun tryAcquire(): Duration? = mutex.withLock {
        val now = clock.now()
        val windowStart = now - timeWindow
        requests.removeAll { it < windowStart }

        if (requests.size < maxRequests) {
            requests.add(now)
            null
        } else {
            requests.first() + timeWindow - now
        }
    }
}
