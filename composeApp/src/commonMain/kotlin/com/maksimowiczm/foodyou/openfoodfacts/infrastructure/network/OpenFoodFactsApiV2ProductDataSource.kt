package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.withRateLimit
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal class OpenFoodFactsApiV2ProductDataSource(
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

        return rateLimiter.withRateLimit(
            onRateLimit = {
                logger.d { "Rate limit exceeded for OpenFoodFacts API" }
                Result.failure(OpenFoodFactsApiError.RateLimitExceeded())
            }
        ) {
            runCatching {
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
                    return@withRateLimit Result.failure(OpenFoodFactsApiError.ProductNotFound())
                }

                val product = response.body<OpenFoodFactsProductResponseV2>()

                product.product
            }
        }
    }

    companion object {
        const val API_URL = "https://world.openfoodfacts.org"
        private const val TAG = "OpenFoodFactsApiV2ProductDataSource"
        private const val TIMEOUT = 60_000L
    }

    @Serializable
    internal data class OpenFoodFactsProductResponseV2(
        @SerialName("product") val product: OpenFoodFactsProductNetwork
    )
}
