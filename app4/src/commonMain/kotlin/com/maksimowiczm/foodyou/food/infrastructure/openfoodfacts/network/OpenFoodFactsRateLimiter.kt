package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network

import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

// 100 req/min for all read product queries (GET /api/v*/product requests or product page). There is
// no limit on product write queries.
// 10 req/min for all search queries (GET /api/v*/search or GET /cgi/search.pl requests); don't use
// it for a search-as-you-type feature, you would be blocked very quickly.
// 2 req/min for facet queries (such as /categories, /label/organic,
// /ingredient/salt/category/breads,...).
class OpenFoodFactsRateLimiter(clock: Clock) {
    private val productRateLimiter = RateLimiter(clock, 100, 1.minutes)
    private val searchRateLimiter = RateLimiter(clock, 10, 1.minutes)

    suspend fun canMakeProductRequest(): Boolean = productRateLimiter.canMakeRequest()

    suspend fun recordProductRequest() = productRateLimiter.recordRequest()

    suspend fun canMakeSearchRequest(): Boolean = searchRateLimiter.canMakeRequest()

    suspend fun recordSearchRequest() = searchRateLimiter.recordRequest()
}
