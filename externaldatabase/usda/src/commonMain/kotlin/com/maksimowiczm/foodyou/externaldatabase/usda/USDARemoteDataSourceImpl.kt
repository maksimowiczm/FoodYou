package com.maksimowiczm.foodyou.externaldatabase.usda

import com.maksimowiczm.foodyou.externaldatabase.usda.model.DetailedFood
import com.maksimowiczm.foodyou.externaldatabase.usda.model.UsdaFoodPageResponseImpl
import com.maksimowiczm.foodyou.feature.usda.BuildConfig
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class USDARemoteDataSourceImpl(private val client: HttpClient) : USDARemoteDataSource {
    override suspend fun getProduct(id: String, apiKey: String?): Result<DetailedFood> {
        try {
            val url = "${BuildConfig.USDA_URL}/fdc/v1/food/$id"

            val response =
                client.get(url) {
                    userAgent(BuildConfig.USER_AGENT)

                    parameter("format", "full")
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                }

            if (response.status == HttpStatusCode.NotFound) {
                FoodYouLogger.d(TAG) { "Product not found for code: $id" }
                return Result.failure(USDAException.ProductNotFoundException())
            }

            if (response.status == HttpStatusCode.TooManyRequests) {
                FoodYouLogger.w(TAG) { "USDA API rate limit exceeded for code: $id" }
                return Result.failure(USDAException.RateLimitException())
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                FoodYouLogger.e(TAG) { "USDA API error for code: $id - ${error.message}" }
                return Result.failure(error)
            }

            val product = response.body<DetailedFood>()

            return Result.success(product)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun queryProducts(
        query: String,
        page: Int?,
        pageSize: Int,
        apiKey: String?,
    ): UsdaFoodPageResponseImpl {
        val url = "${BuildConfig.USDA_URL}/fdc/v1/foods/search"

        val response =
            client.get(url) {
                userAgent(BuildConfig.USER_AGENT)

                parameter("query", query)
                parameter("dataType", "Branded,Foundation")
                parameter("pageSize", pageSize)
                parameter("pageNumber", page)
                parameter("api_key", apiKey ?: "DEMO_KEY")
                parameter("sortBy", "dataType.keyword")
                parameter("sortOrder", "asc")
            }

        if (response.status == HttpStatusCode.TooManyRequests) {
            throw USDAException.RateLimitException()
        }

        if (response.status == HttpStatusCode.Forbidden) {
            val error = response.getError()
            throw error
        }

        return response.body()
    }

    private suspend fun HttpResponse.getError(): Exception =
        with(body<String>()) {
            return when {
                contains("API_KEY_MISSING") -> USDAException.ApiKeyIsMissingException()
                contains("API_KEY_INVALID") -> USDAException.ApiKeyInvalidException()
                contains("API_KEY_DISABLED") -> USDAException.ApiKeyDisabledException()
                contains("API_KEY_UNAUTHORIZED") -> USDAException.ApiKeyUnauthorizedException()
                contains("API_KEY_UNVERIFIED") -> USDAException.ApiKeyUnverifiedException()
                else -> Exception("Unknown USDA API error: $this")
            }
        }

    private companion object {
        private const val TAG = "USDARemoteDataSourceImpl"
    }
}
