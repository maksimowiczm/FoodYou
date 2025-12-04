package com.maksimowiczm.foodyou.food.infrastructure.usda.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.infrastructure.usda.network.model.DetailedFood
import com.maksimowiczm.foodyou.food.infrastructure.usda.network.model.FoodDataCentralFoodPageResponse
import com.maksimowiczm.foodyou.food.infrastructure.usda.network.model.FoodDataCentralFoodPageResponseImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class FoodDataCentralRemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val rateLimiter: FoodDataCentralRateLimiter,
    logger: Logger,
) {
    private val logger = logger.withTag(TAG)

    suspend fun getProduct(id: Int, apiKey: String?): Result<DetailedFood> {
        try {
            val url = "$API_URL/v1/food/$id"

            if (!rateLimiter.canMakeRequest()) {
                logger.d { "Rate limit exceeded for FoodDataCentral API" }
                return Result.failure(FoodDatabaseError.FoodDataCentral.RateLimitExceeded)
            }

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)

                    parameter("format", "full")
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                }

            if (response.status == HttpStatusCode.NotFound) {
                logger.d { "Product not found for code: $id" }
                return Result.failure(FoodDatabaseError.ProductNotFound)
            }

            if (response.status == HttpStatusCode.TooManyRequests) {
                logger.w { "FoodDataCentral API rate limit exceeded for code: $id" }
                return Result.failure(FoodDatabaseError.FoodDataCentral.RateLimitExceeded)
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                logger.e { "FoodDataCentral API error for code: $id - ${error.message}" }
                return Result.failure(error)
            }

            val product = response.body<DetailedFood>()

            return Result.success(product)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return when (e) {
                is FoodDatabaseError -> Result.failure(e)
                else -> Result.failure(FoodDatabaseError.Unknown(e.message))
            }
        } finally {
            rateLimiter.recordRequest()
        }
    }

    suspend fun queryProducts(
        query: String,
        page: Int?,
        pageSize: Int,
        apiKey: String?,
    ): FoodDataCentralFoodPageResponse =
        try {
            if (!rateLimiter.canMakeRequest()) {
                logger.d { "Rate limit exceeded for FoodDataCentral API" }
                throw FoodDatabaseError.FoodDataCentral.RateLimitExceeded
            }

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
                throw FoodDatabaseError.FoodDataCentral.RateLimitExceeded
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                throw error
            }

            response.body<FoodDataCentralFoodPageResponseImpl>()
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is FoodDatabaseError -> throw e
                else -> throw FoodDatabaseError.Unknown(e.message)
            }
        } finally {
            rateLimiter.recordRequest()
        }

    private suspend fun HttpResponse.getError(): Exception =
        with(body<String>()) {
            when {
                contains("API_KEY_MISSING") -> FoodDatabaseError.FoodDataCentral.ApiKeyIsMissing
                contains("API_KEY_INVALID") -> FoodDatabaseError.FoodDataCentral.ApiKeyInvalid
                contains("API_KEY_DISABLED") -> FoodDatabaseError.FoodDataCentral.ApiKeyDisabled
                contains("API_KEY_UNAUTHORIZED") ->
                    FoodDatabaseError.FoodDataCentral.ApiKeyUnauthorized

                contains("API_KEY_UNVERIFIED") -> FoodDatabaseError.FoodDataCentral.ApiKeyUnverified
                else -> Exception("Unknown FoodDataCentral API error: $this")
            }
        }

    private companion object {
        private const val TAG = "FoodDataCentralRemoteDataSourceImpl"
        private const val API_URL = "https://api.nal.usda.gov/fdc"
    }
}
