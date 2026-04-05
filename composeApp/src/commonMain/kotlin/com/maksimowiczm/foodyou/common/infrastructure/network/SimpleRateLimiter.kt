package com.maksimowiczm.foodyou.common.infrastructure.network

/**
 * A [RateLimiter] that immediately rejects requests when the rate limit is exceeded.
 *
 * @param log The [WindowedRequestLog] used to track and enforce the rate limit.
 */
class SimpleRateLimiter(private val log: WindowedRequestLog) : RateLimiter {
    override suspend fun acquire(): Boolean = log.tryAcquire() == null
}
