package com.maksimowiczm.foodyou.feature.usda

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.usda.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.feature.usda.model.NullableAbridgedFoodItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent

internal class USDARemoteDataSourceImpl(private val client: HttpClient) : USDARemoteDataSource {
    override suspend fun getProduct(id: String, apiKey: String): Result<AbridgedFoodItem> {
        try {
            val url = "${BuildConfig.USDA_URL}/fdc/v1/food/$id"

            val response = client.get(url) {
                userAgent(BuildConfig.USER_AGENT)

                parameter("format", "abridged")
                parameter("api_key", apiKey)
            }

            if (response.status == HttpStatusCode.NotFound) {
                Logger.d(TAG) { "Product not found for code: $id" }
                return Result.failure(USDAException.ProductNotFoundException())
            }

            if (response.status == HttpStatusCode.TooManyRequests) {
                Logger.w(TAG) { "USDA API rate limit exceeded for code: $id" }
                return Result.failure(USDAException.RateLimitException())
            }

            if (response.status == HttpStatusCode.Forbidden) {
                val error = response.getError()
                Logger.e(TAG) { "USDA API error for code: $id - ${error.message}" }
                return Result.failure(error)
            }

            val product = response.body<NullableAbridgedFoodItem>()

            return Result.success(product)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private suspend fun HttpResponse.getError(): Exception = with(body<String>()) {
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
