package com.maksimowiczm.foodyou.food.infrastructure.usda.network

import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class FoodDataCentralRateLimiter(clock: Clock) {
    private val rateLimiter = RateLimiter(clock, 30, 1.hours)

    suspend fun canMakeRequest(): Boolean = rateLimiter.canMakeRequest()

    suspend fun recordRequest() = rateLimiter.recordRequest()
}
