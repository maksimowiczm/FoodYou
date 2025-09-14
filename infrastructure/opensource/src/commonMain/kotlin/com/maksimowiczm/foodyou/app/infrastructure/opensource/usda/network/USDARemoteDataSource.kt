package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.NetworkConfig
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.model.DetailedFood
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.model.UsdaFoodPageResponse
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.model.UsdaFoodPageResponseImpl
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlin.coroutines.cancellation.CancellationException

internal class USDARemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val logger: Logger,
) {
    suspend fun getProduct(id: String, apiKey: String?): Result<DetailedFood> {
        try {
            val url = "${networkConfig.usdaApiUrl}/fdc/v1/food/$id"

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)

                    parameter("format", "full")
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                }

            if (response.status == HttpStatusCode.Companion.NotFound) {
                logger.d(TAG) { "Product not found for code: $id" }
                return Result.failure(RemoteFoodException.ProductNotFoundException())
            }

            if (response.status == HttpStatusCode.Companion.TooManyRequests) {
                logger.w(TAG) { "USDA API rate limit exceeded for code: $id" }
                return Result.failure(RemoteFoodException.USDA.RateLimitException())
            }

            if (response.status == HttpStatusCode.Companion.Forbidden) {
                val error = response.getError()
                logger.e(TAG) { "USDA API error for code: $id - ${error.message}" }
                return Result.failure(error)
            }

            val product = response.body<DetailedFood>()

            return Result.success(product)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is RemoteFoodException -> throw e
                else -> throw RemoteFoodException.Unknown(e.message)
            }
        }
    }

    suspend fun queryProducts(
        query: String,
        page: Int?,
        pageSize: Int,
        apiKey: String?,
    ): UsdaFoodPageResponse =
        try {
            val url = "${networkConfig.usdaApiUrl}/fdc/v1/foods/search"

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)

                    parameter("query", query)
                    parameter("dataType", "Branded,Foundation")
                    parameter("pageSize", pageSize)
                    parameter("pageNumber", page)
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                    parameter("sortBy", "dataType.keyword")
                    parameter("sortOrder", "asc")
                }

            if (response.status == HttpStatusCode.Companion.TooManyRequests) {
                throw RemoteFoodException.USDA.RateLimitException()
            }

            if (response.status == HttpStatusCode.Companion.Forbidden) {
                val error = response.getError()
                throw error
            }

            response.body<UsdaFoodPageResponseImpl>()
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is RemoteFoodException -> throw e
                else -> throw RemoteFoodException.Unknown(e.message)
            }
        }

    private suspend fun HttpResponse.getError(): Exception =
        with(body<String>()) {
            return when {
                contains("API_KEY_MISSING") -> RemoteFoodException.USDA.ApiKeyIsMissingException()
                contains("API_KEY_INVALID") -> RemoteFoodException.USDA.ApiKeyInvalidException()
                contains("API_KEY_DISABLED") -> RemoteFoodException.USDA.ApiKeyDisabledException()
                contains("API_KEY_UNAUTHORIZED") ->
                    RemoteFoodException.USDA.ApiKeyUnauthorizedException()

                contains("API_KEY_UNVERIFIED") ->
                    RemoteFoodException.USDA.ApiKeyUnverifiedException()

                else -> Exception("Unknown USDA API error: $this")
            }
        }

    private companion object {
        private const val TAG = "USDARemoteDataSourceImpl"
    }
}
