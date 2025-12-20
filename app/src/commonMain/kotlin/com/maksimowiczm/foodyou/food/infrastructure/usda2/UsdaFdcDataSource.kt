package com.maksimowiczm.foodyou.food.infrastructure.usda2

import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.infrastructure.usda2.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.food.infrastructure.usda2.model.SearchResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class UsdaFdcDataSource(
    private val client: HttpClient,
    private val networkConfig: NetworkConfig,
    private val logger: Logger,
    private val apiKey: String? = null,
) {

    suspend fun getFood(
        fdcId: String,
        format: String?,
        nutrients: List<Int>?,
    ): Result<AbridgedFoodItem> {
        return try {
            val url = "${networkConfig.usdaApiUrl}/fdc/v1/food/$fdcId"

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                    format?.let { parameter("format", it) }
                    nutrients?.let { parameter("nutrients", it.joinToString(",")) }
                }

            handleResponse(response) { response.body<AbridgedFoodItem>() }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            handleException(e, "getFood", fdcId)
        }
    }

    suspend fun getFoodsSearch(
        query: String,
        dataType: List<String>?,
        pageSize: Int?,
        pageNumber: Int?,
        sortBy: String?,
        sortOrder: String?,
        brandOwner: String?,
    ): Result<SearchResult> {
        return try {
            val url = "${networkConfig.usdaApiUrl}/fdc/v1/foods/search"

            val response =
                client.get(url) {
                    userAgent(networkConfig.userAgent)
                    parameter("api_key", apiKey ?: "DEMO_KEY")
                    parameter("query", query)
                    dataType?.let { parameter("dataType", it.joinToString(",")) }
                    pageSize?.let { parameter("pageSize", it) }
                    pageNumber?.let { parameter("pageNumber", it) }
                    sortBy?.let { parameter("sortBy", it) }
                    sortOrder?.let { parameter("sortOrder", it) }
                    brandOwner?.let { parameter("brandOwner", it) }
                }

            handleResponse(response) { response.body<SearchResult>() }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            handleException(e, "getFoodsSearch", query)
        }
    }

    private suspend fun <T> handleResponse(
        response: HttpResponse,
        onSuccess: suspend () -> T,
    ): Result<T> {
        return when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(onSuccess())
            }
            HttpStatusCode.NotFound -> {
                logger.d(TAG) { "Product not found" }
                Result.failure(RemoteFoodException.ProductNotFoundException())
            }
            HttpStatusCode.TooManyRequests -> {
                logger.w(TAG) { "USDA API rate limit exceeded" }
                Result.failure(RemoteFoodException.USDA.RateLimitException())
            }
            HttpStatusCode.Forbidden -> {
                val error = response.getError()
                logger.e(TAG) { "USDA API error: ${error.message}" }
                Result.failure(error)
            }
            HttpStatusCode.BadRequest -> {
                val body = response.bodyAsText()
                logger.e(TAG) { "Bad request: $body" }
                Result.failure(RemoteFoodException.Unknown("Bad request parameter"))
            }
            else -> {
                logger.e(TAG) { "Unexpected response: ${response.status}" }
                Result.failure(
                    RemoteFoodException.Unknown("Unexpected response: ${response.status}")
                )
            }
        }
    }

    private suspend fun HttpResponse.getError(): Exception {
        val body = bodyAsText()
        return when {
            body.contains("API_KEY_MISSING") -> RemoteFoodException.USDA.ApiKeyIsMissingException()
            body.contains("API_KEY_INVALID") -> RemoteFoodException.USDA.ApiKeyInvalidException()
            body.contains("API_KEY_DISABLED") -> RemoteFoodException.USDA.ApiKeyDisabledException()
            body.contains("API_KEY_UNAUTHORIZED") ->
                RemoteFoodException.USDA.ApiKeyUnauthorizedException()
            body.contains("API_KEY_UNVERIFIED") ->
                RemoteFoodException.USDA.ApiKeyUnverifiedException()
            else -> Exception("Unknown USDA API error: $body")
        }
    }

    private fun <T> handleException(e: Exception, method: String, context: String): Result<T> {
        return when (e) {
            is CancellationException -> throw e
            is RemoteFoodException -> {
                logger.e(TAG) { "$method failed for $context: ${e.message}" }
                Result.failure(e)
            }
            else -> {
                logger.e(TAG) { "$method failed for $context: ${e.message}" }
                Result.failure(RemoteFoodException.Unknown(e.message))
            }
        }
    }

    private companion object {
        private const val TAG = "UsdaFdcApi"
    }
}
