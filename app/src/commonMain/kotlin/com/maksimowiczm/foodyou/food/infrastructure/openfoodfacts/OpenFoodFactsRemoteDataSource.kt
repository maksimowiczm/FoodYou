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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.userAgent
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal class OpenFoodFactsRemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val rateLimiter: OpenFoodFactsRateLimiter,
    private val credentialsRepository: OpenFoodFactsCredentialsRepositoryImpl,
    private val logger: Logger,
) {
    suspend fun getProduct(
        barcode: String,
        countries: String? = null,
    ): Result<OpenFoodFactsProduct> {
        try {
            val countries = countries?.lowercase()
            val url = "${API_URL}/api/v2/product/$barcode"

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
        queryProducts(
            query = query,
            shouldLogin = false,
            countries = countries,
            page = page,
            pageSize = pageSize,
        )

    private suspend fun queryProducts(
        query: String,
        shouldLogin: Boolean,
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

            if (shouldLogin) {
                val credentials =
                    credentialsRepository.loadCredentials()
                        ?: throw RemoteFoodException.OpenFoodFacts.ServiceUnavailable()

                client.post("${API_URL}/cgi/session.pl") {
                    userAgent(networkConfig.userAgent)
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(
                        Parameters.build {
                                append("user_id", credentials.first)
                                append("password", credentials.second)
                            }
                            .formUrlEncode()
                    )
                }
            }

            val response =
                client.get("${API_URL}/cgi/search.pl?search_simple=1&json=1") {
                    userAgent(networkConfig.userAgent)
                    parameter("search_terms", query)
                    parameter("countries", countries)
                    parameter("page", page)
                    parameter("page_size", pageSize)
                    parameter("sort_by", "product_name")
                    parameter("fields", FIELDS)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                }

            if (response.status == HttpStatusCode.ServiceUnavailable) {
                if (!shouldLogin) {
                    queryProducts(
                        query = query,
                        shouldLogin = true,
                        countries = countries,
                        page = page,
                        pageSize = pageSize,
                    )
                } else {
                    throw RemoteFoodException.OpenFoodFacts.ServiceUnavailable()
                }
            } else {
                response.body<OpenFoodFactsPageResponseV1>()
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is RemoteFoodException -> throw e
                else -> throw RemoteFoodException.Unknown(e.message)
            }
        }

    private companion object {
        private const val API_URL = "https://world.openfoodfacts.org"
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
