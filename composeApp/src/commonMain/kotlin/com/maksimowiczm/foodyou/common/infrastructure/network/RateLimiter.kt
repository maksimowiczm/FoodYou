package com.maksimowiczm.foodyou.common.infrastructure.network

/** A rate limiter that controls access to a resource by limiting the number of requests. */
interface RateLimiter {

    /**
     * Attempts to acquire a slot for a request.
     *
     * @return `true` if the request is allowed, `false` if the rate limit has been exceeded.
     */
    suspend fun acquire(): Boolean
}

/**
 * Executes [block] if the rate limit allows, otherwise executes [onRateLimit].
 *
 * @param onRateLimit Called when the rate limit has been exceeded.
 * @param block The action to perform if the request is allowed.
 * @return The result of either [block] or [onRateLimit].
 */
suspend fun <T> RateLimiter.withRateLimit(onRateLimit: suspend () -> T, block: suspend () -> T): T =
    if (acquire()) block() else onRateLimit()
