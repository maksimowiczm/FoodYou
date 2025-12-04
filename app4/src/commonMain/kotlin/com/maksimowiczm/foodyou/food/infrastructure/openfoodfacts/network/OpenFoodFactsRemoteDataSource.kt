package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.v2.OpenFoodFactsProductResponseV2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class OpenFoodFactsRemoteDataSource(
    private val client: HttpClient,
    private val rateLimiter: OpenFoodFactsRateLimiter,
    private val networkConfig: NetworkConfig,
    logger: Logger,
) {
    private val logger = logger.withTag(TAG)

    suspend fun getProduct(
        barcode: String,
        countries: String? = null,
    ): Result<OpenFoodFactsProduct> {
        try {
            val countries = countries?.lowercase()
            val url = "$API_URL/api/v2/product/$barcode"

            if (!rateLimiter.canMakeProductRequest()) {
                logger.d { "Rate limit exceeded for OpenFoodFacts API" }
                return Result.failure(FoodDatabaseError.OpenFoodFacts.RateLimitExceeded)
            }

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                    countries?.let { parameter("countries", countries) }
                }

            if (response.status == HttpStatusCode.NotFound) {
                logger.d { "Product not found for code: $barcode" }
                return Result.failure(FoodDatabaseError.ProductNotFound)
            }

            val product = response.body<OpenFoodFactsProductResponseV2>()

            return Result.success(product).map { it.product }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return when (e) {
                is FoodDatabaseError -> Result.failure(e)
                else -> Result.failure(FoodDatabaseError.Unknown(e.message))
            }
        } finally {
            rateLimiter.recordProductRequest()
        }
    }

    suspend fun queryProducts(
        query: String,
        countries: String? = null,
        page: Int? = null,
        pageSize: Int = 50,
    ): OpenFoodPageResponse =
        try {
            if (!rateLimiter.canMakeSearchRequest()) {
                logger.d { "Rate limit exceeded for OpenFoodFacts API" }
                throw FoodDatabaseError.OpenFoodFacts.RateLimitExceeded
            }

            client
                .get("$API_URL/cgi/search.pl?search_simple=1&json=1") {
                    userAgent(networkConfig.userAgent)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                    parameter("search_terms", query)
                    parameter("countries", countries)
                    parameter("page", page)
                    parameter("page_size", pageSize)
                    parameter("sort_by", "product_name")
                }
                .body<OpenFoodFactsPageResponseV1>()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()

            when (e) {
                is FoodDatabaseError -> throw e
                else -> throw FoodDatabaseError.Unknown(e.message)
            }
        } finally {
            rateLimiter.recordSearchRequest()
        }

    private companion object {
        const val API_URL = "https://world.openfoodfacts.org"
        private const val TAG = "OpenFoodFactsRemoteDataSource"
        private const val TIMEOUT = 60_000L
    }
}
