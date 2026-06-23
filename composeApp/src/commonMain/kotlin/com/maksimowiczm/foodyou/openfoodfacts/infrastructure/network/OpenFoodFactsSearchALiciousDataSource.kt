package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.common.infrastructure.network.withRateLimit
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote data source for the SearchALicious API.
 *
 * SearchALicious is a search service built on top of Elasticsearch that provides fast and
 * comprehensive product searches for Open Food Facts.
 */
internal class OpenFoodFactsSearchALiciousDataSource(
    private val client: HttpClient,
    private val rateLimiter: RateLimiter,
    private val networkConfig: NetworkConfig,
    logger: Logger,
) : OpenFoodFactsSearchDataSource {
    private val logger = logger.withTag(TAG)

    /**
     * Search for products using the SearchALicious API.
     *
     * @param query The search query, supports Lucene query syntax. Optional if sortBy is provided.
     * @param pageSize Number of results to return per page. Defaults to 10.
     * @param page Page number to request, starts at 1. Defaults to 1.
     * @param sortBy Field name to sort results by. Prefix with "-" for descending order.
     * @return SearchALiciousResponse containing the search results with raw JSON populated for each
     *   hit
     */
    override suspend fun search(
        query: String,
        pageSize: Int,
        page: Int,
        sortBy: String?,
    ): OpenFoodFactsNetworkPage {
        return rateLimiter.withRateLimit(
            onRateLimit = {
                logger.d { "Rate limit exceeded for OpenFoodFacts API" }
                throw OpenFoodFactsApiError.RateLimitExceeded()
            }
        ) {
            client
                .get("$API_URL/search") {
                    userAgent(networkConfig.userAgent)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }

                    // Add query parameters
                    parameter("q", query)
                    parameter("page_size", pageSize)
                    parameter("page", page)
                    sortBy?.let { parameter("sort_by", it) }
                }
                .body<Page>()
        }
    }

    companion object {
        private const val API_URL = "https://search.openfoodfacts.org"
        private const val TAG = "OpenFoodFactsSearchALiciousDataSource"
        private const val TIMEOUT = 60_000L
    }

    @Serializable
    private data class Page(
        @SerialName("hits") override val products: List<OpenFoodFactsProductNetwork>,
        @SerialName("page") override val page: Int,
        @SerialName("page_size") override val pageSize: Int,
        @SerialName("page_count") val pageCount: Int,
        @SerialName("count") override val count: Int,
        @SerialName("is_count_exact") val isCountExact: Boolean,
        @SerialName("took") val took: Int? = null,
        @SerialName("timed_out") val timedOut: Boolean? = null,
    ) : OpenFoodFactsNetworkPage
}
