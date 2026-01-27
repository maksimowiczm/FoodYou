package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network

import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

internal class FoodDataCentralRateLimiter(clock: Clock) {
    private val rateLimiter = RateLimiter(clock, 30, 1.hours)

    suspend fun canMakeRequest(): Boolean = rateLimiter.canMakeRequest()

    suspend fun recordRequest() = rateLimiter.recordRequest()
}
