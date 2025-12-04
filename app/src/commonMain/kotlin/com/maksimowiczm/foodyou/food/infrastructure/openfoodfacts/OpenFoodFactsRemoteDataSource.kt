package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.model.v2.OpenFoodFactsProductResponseV2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal class OpenFoodFactsRemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val rateLimiter: OpenFoodFactsRateLimiter,
    private val logger: Logger,
) {
    suspend fun getProduct(
        barcode: String,
        countries: String? = null,
    ): Result<OpenFoodFactsProduct> {
        try {
            val countries = countries?.lowercase()
            val url = "${networkConfig.openFoodFactsApiUrl}/api/v2/product/$barcode"

            if (!rateLimiter.canMakeProductRequest()) {
                logger.d(TAG) { "Rate limit exceeded for OpenFoodFacts API" }
                return Result.failure(RemoteFoodException.OpenFoodFacts.RateLimit())
            }

            rateLimiter.recordProductRequest()

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                    countries?.let { parameter("countries", countries) }
                    parameter("fields", FIELDS)
                }

            if (response.status == HttpStatusCode.NotFound) {
                logger.d(TAG) { "Product not found for code: $barcode" }
                return Result.failure(RemoteFoodException.ProductNotFoundException())
            }

            val product = response.body<OpenFoodFactsProductResponseV2>()

            return Result.success(product).map { it.product }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return when (e) {
                is RemoteFoodException -> Result.failure(e)
                else -> Result.failure(RemoteFoodException.Unknown(e.message))
            }
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
                logger.d(TAG) { "Rate limit exceeded for OpenFoodFacts API" }
                throw RemoteFoodException.OpenFoodFacts.RateLimit()
            }

            rateLimiter.recordSearchRequest()

            client
                .get("${networkConfig.openFoodFactsApiUrl}/cgi/search.pl?search_simple=1&json=1") {
                    parameter("search_terms", query)
                    parameter("countries", countries)
                    parameter("page", page)
                    parameter("page_size", pageSize)
                    parameter("sort_by", "product_name")
                    parameter("fields", FIELDS)
                }
                .body<OpenFoodFactsPageResponseV1>()
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is RemoteFoodException -> throw e
                else -> throw RemoteFoodException.Unknown(e.message)
            }
        }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteDataSource"
        private const val TIMEOUT = 60_000L
    }
}

private const val FIELDS =
    "" +
        "product_name" +
        ",code" +
        ",nutriments" +
        ",brands" +
        ",serving_quantity" +
        ",serving_quantity_unit" +
        ",product_quantity" +
        ",product_quantity_unit" +
        ",url"
