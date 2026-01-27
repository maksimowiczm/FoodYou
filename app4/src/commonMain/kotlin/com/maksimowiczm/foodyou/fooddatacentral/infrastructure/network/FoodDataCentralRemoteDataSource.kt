package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.DetailedFood
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.FoodDataCentralFoodPageResponse
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.FoodDataCentralFoodPageResponseImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class FoodDataCentralRemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val rateLimiter: FoodDataCentralRateLimiter,
    logger: Logger,
) {
    private val logger = logger.withTag(TAG)

    suspend fun getProduct(id: Int, apiKey: String?): Result<DetailedFood> {
        val url = "$API_URL/v1/food/$id"

        if (!rateLimiter.canMakeRequest()) {
            logger.d { "Rate limit exceeded for FoodDataCentral API" }
            return Result.failure(FoodDataCentralApiError.RateLimitExceeded())
        }

        return runCatching {
                val response =
                    client.get(url) {
                        userAgent(networkConfig.userAgent)

                        parameter("format", "full")
                        parameter("api_key", apiKey ?: "DEMO_KEY")
                    }

                if (response.status == HttpStatusCode.NotFound) {
                    logger.d { "Product not found for code: $id" }
                    return Result.failure(FoodDataCentralApiError.ProductNotFound())
                }

                if (response.status == HttpStatusCode.TooManyRequests) {
                    logger.w { "FoodDataCentral API rate limit exceeded for code: $id" }
                    return Result.failure(FoodDataCentralApiError.RateLimitExceeded())
                }

                if (response.status == HttpStatusCode.Forbidden) {
                    val error = response.getError()
                    logger.e { "FoodDataCentral API error for code: $id - ${error.message}" }
                    return Result.failure(error)
                }

                response.body<DetailedFood>()
            }
            .also { rateLimiter.recordRequest() }
    }

    suspend fun queryProducts(
        query: String,
        page: Int?,
        pageSize: Int,
        apiKey: String?,
    ): FoodDataCentralFoodPageResponse {
        if (!rateLimiter.canMakeRequest()) {
            logger.d { "Rate limit exceeded for FoodDataCentral API" }
            throw FoodDataCentralApiError.RateLimitExceeded()
        }

        return try {
            val response =
                client.get("$API_URL/v1/foods/search") {
                    userAgent(networkConfig.userAgent)
                    parameter("query", query)
                    parameter("dataType", "Branded,Foundation")
                    parameter("pageSize", pageSize)
                    parameter("pageNumber", page)
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                    parameter("sortBy", "dataType.keyword")
                    parameter("sortOrder", "asc")
                }

            if (response.status == HttpStatusCode.TooManyRequests) {
                throw FoodDataCentralApiError.RateLimitExceeded()
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                throw error
            }

            response.body<FoodDataCentralFoodPageResponseImpl>()
        } finally {
            rateLimiter.recordRequest()
        }
    }

    private suspend fun HttpResponse.getError(): Exception =
        with(body<String>()) {
            when {
                contains("API_KEY_MISSING") -> FoodDataCentralApiError.ApiKeyIsMissing()
                contains("API_KEY_INVALID") -> FoodDataCentralApiError.ApiKeyInvalid()
                contains("API_KEY_DISABLED") -> FoodDataCentralApiError.ApiKeyDisabled()
                contains("API_KEY_UNAUTHORIZED") -> FoodDataCentralApiError.ApiKeyUnauthorized()

                contains("API_KEY_UNVERIFIED") -> FoodDataCentralApiError.ApiKeyUnverified()
                else -> Exception("Unknown FoodDataCentral API error: $this")
            }
        }

    private companion object {
        private const val TAG = "FoodDataCentralRemoteDataSourceImpl"
        private const val API_URL = "https://api.nal.usda.gov/fdc"
    }
}
