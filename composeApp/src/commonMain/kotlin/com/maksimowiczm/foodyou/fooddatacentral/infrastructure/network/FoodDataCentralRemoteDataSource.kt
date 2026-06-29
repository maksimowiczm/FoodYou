package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.SuspendingRateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.WindowedRequestLog
import com.maksimowiczm.foodyou.common.infrastructure.network.withRateLimit
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiKeyVerificationService
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.DetailedFood
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.FoodDataCentralFoodPageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class FoodDataCentralRemoteDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val rateLimiter: RateLimiter,
    logger: Logger,
) : FoodDataCentralApiKeyVerificationService {
    private val logger = logger.withTag(TAG)

    override suspend fun verify(apiKey: String) {
        val _ = getProduct(id = 67, apiKey = apiKey).getOrThrow()
    }

    suspend fun getProduct(id: Int, apiKey: String?): Result<DetailedFood> {
        val url = "$API_URL/v1/food/$id"

        return rateLimiter.withRateLimit(
            onRateLimit = {
                logger.d { "Rate limit exceeded for FoodDataCentral API" }
                Result.failure(FoodDataCentralApiError.RateLimitExceeded())
            }
        ) {
            runCatching {
                val response =
                    client.get(url) {
                        userAgent(networkConfig.userAgent)
                        parameter("format", "full")
                        parameter("api_key", apiKey ?: "DEMO_KEY")
                    }

                if (response.status == HttpStatusCode.NotFound) {
                    logger.d { "Product not found for code: $id" }
                    return@withRateLimit Result.failure(FoodDataCentralApiError.ProductNotFound())
                }

                if (response.status == HttpStatusCode.TooManyRequests) {
                    logger.w { "FoodDataCentral API rate limit exceeded for code: $id" }
                    return@withRateLimit Result.failure(FoodDataCentralApiError.RateLimitExceeded())
                }

                if (response.status == HttpStatusCode.Forbidden) {
                    val error = response.getError()
                    logger.e { "FoodDataCentral API error for code: $id - ${error.message}" }
                    return@withRateLimit Result.failure(error)
                }

                response.body<DetailedFood>()
            }
        }
    }

    suspend fun queryProducts(
        query: String,
        page: Int?,
        pageSize: Int,
        apiKey: String?,
        dataTypes: List<String>?,
    ): FoodDataCentralFoodPageResponse {
        return rateLimiter.withRateLimit(
            onRateLimit = {
                logger.d { "Rate limit exceeded for FoodDataCentral API" }
                throw FoodDataCentralApiError.RateLimitExceeded()
            }
        ) {
            val response =
                client.get("$API_URL/v1/foods/search") {
                    userAgent(networkConfig.userAgent)
                    parameter("query", query)
                    parameter("pageSize", pageSize)
                    parameter("pageNumber", page)
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                    parameter("sortBy", "dataType.keyword")
                    parameter("sortOrder", "asc")
                    dataTypes?.let { parameter("dataType", dataTypes.joinToString(",")) }
                }

            if (response.status == HttpStatusCode.TooManyRequests) {
                throw FoodDataCentralApiError.RateLimitExceeded()
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                throw error
            }

            response.body<FoodDataCentralFoodPageResponse>()
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

    companion object {
        private const val TAG = "FoodDataCentralRemoteDataSourceImpl"
        private const val API_URL = "https://api.nal.usda.gov/fdc"

        fun rateLimiter(clock: Clock = Clock.System): RateLimiter =
            SuspendingRateLimiter(
                clock = clock,
                log = WindowedRequestLog(30, 1.hours, clock),
                timeout = 1.seconds,
                minWaitTime = 100.milliseconds,
            )
    }
}
