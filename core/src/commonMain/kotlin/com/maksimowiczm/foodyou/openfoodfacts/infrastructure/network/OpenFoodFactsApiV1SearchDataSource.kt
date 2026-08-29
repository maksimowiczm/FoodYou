package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.withRateLimit
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsLoginService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.get
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
import io.ktor.http.setCookie
import io.ktor.http.userAgent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal class OpenFoodFactsApiV1SearchDataSource(
    private val client: HttpClient,
    private val rateLimiter: RateLimiter,
    private val networkConfig: NetworkConfig,
    logger: Logger,
) : OpenFoodFactsSearchDataSource, OpenFoodFactsLoginService {
    private val logger = logger.withTag(TAG)

    override suspend fun search(
        query: String,
        credentials: OpenFoodFactsCredentials.Decrypted?,
        pageSize: Int,
        page: Int,
        sortBy: String?,
    ) =
        rateLimiter.withRateLimit(
            onRateLimit = {
                logger.d { "Rate limit exceeded for OpenFoodFacts API" }
                throw OpenFoodFactsApiError.RateLimitExceeded()
            }
        ) {
            search(
                query = query,
                credentials = credentials,
                shouldLogin = false,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
            )
        }

    // We will miss one rate limit in case of login, but rate limiter might not allow nested
    // limitting.
    private suspend fun search(
        query: String,
        shouldLogin: Boolean,
        credentials: OpenFoodFactsCredentials.Decrypted?,
        page: Int? = null,
        pageSize: Int = 50,
        sortBy: String? = null,
    ): OpenFoodFactsNetworkPage =
        try {
            if (shouldLogin) {
                if (credentials != null) {
                    val _ = runCatching { login(credentials.login, credentials.password) }
                }
            }

            val response =
                client.get("${API_URL}/cgi/search.pl?search_simple=1&json=1") {
                    userAgent(networkConfig.userAgent)
                    parameter("search_terms", query)
                    parameter("page", page)
                    parameter("page_size", pageSize)
                    sortBy?.let { parameter("sort_by", it) }
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                }

            if (response.status == HttpStatusCode.ServiceUnavailable) {
                if (!shouldLogin) {
                    search(
                        query = query,
                        credentials = credentials,
                        shouldLogin = true,
                        page = page,
                        pageSize = pageSize,
                        sortBy = sortBy,
                    )
                } else {
                    throw OpenFoodFactsApiError.ServiceUnavailable()
                }
            } else {
                response.body<Page>()
            }
        } catch (e: Exception) {
            if (e is OpenFoodFactsApiError) throw e
            throw OpenFoodFactsApiError.Unknown(e)
        }

    override suspend fun login(username: String, password: String) {
        val response =
            client.post("${API_URL}/cgi/session.pl") {
                userAgent(networkConfig.userAgent)
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    Parameters.build {
                            append("user_id", username)
                            append("password", password)
                        }
                        .formUrlEncode()
                )
            }

        val cookies = response.setCookie()
        checkNotNull(cookies["session"]) { "Session cookie not found" }
    }

    private companion object {
        private const val API_URL = "https://world.openfoodfacts.org"
        private const val TAG = "OpenFoodFactsLegacySearchDataSource"
        private const val TIMEOUT = 60_000L
    }

    @Serializable
    private data class Page(
        @SerialName("count") override val count: Int,
        @SerialName("page") override val page: Int,
        @SerialName("page_size") override val pageSize: Int,
        @SerialName("products") override val products: List<OpenFoodFactsProductNetwork>,
    ) : OpenFoodFactsNetworkPage
}
