package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.SuspendingRateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.WindowedRequestLog
import com.maksimowiczm.foodyou.common.infrastructure.network.withRateLimit
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
        private const val TAG = "OpenFoodFactsRemoteDataSource"
        private const val TIMEOUT = 60_000L

        fun rateLimiter(clock: Clock): RateLimiter =
            SuspendingRateLimiter(
                clock = clock,
                log = WindowedRequestLog(clock, 100, 1.minutes),
                timeout = 1.seconds,
                minWaitTime = 100.milliseconds,
            )
    }
}
