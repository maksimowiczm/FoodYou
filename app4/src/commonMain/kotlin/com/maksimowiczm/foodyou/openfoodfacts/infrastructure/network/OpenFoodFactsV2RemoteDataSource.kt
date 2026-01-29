package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsProductNetwork
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsProductResponseV2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

internal class OpenFoodFactsV2RemoteDataSource(
    private val client: HttpClient,
    private val rateLimiter: RateLimiter,
    private val networkConfig: NetworkConfig,
    logger: Logger,
) {
    private val logger = logger.withTag(TAG)

    suspend fun getProduct(
        barcode: String,
        countries: String? = null,
    ): Result<OpenFoodFactsProductNetwork> {
        val countries = countries?.lowercase()
        val url = "$API_URL/api/v2/product/$barcode"

        if (!rateLimiter.canMakeRequest()) {
            logger.d { "Rate limit exceeded for OpenFoodFacts API" }
            return Result.failure(OpenFoodFactsApiError.RateLimitExceeded())
        }

        return runCatching {
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
                    return Result.failure(OpenFoodFactsApiError.ProductNotFound())
                }

                val product = response.body<OpenFoodFactsProductResponseV2>()

                product.product
            }
            .also { rateLimiter.recordRequest() }
    }

    companion object {
        const val API_URL = "https://world.openfoodfacts.org"
        private const val TAG = "OpenFoodFactsRemoteDataSource"
        private const val TIMEOUT = 60_000L

        fun rateLimiter(clock: Clock) = RateLimiter(clock, 100, 1.minutes)
    }
}
