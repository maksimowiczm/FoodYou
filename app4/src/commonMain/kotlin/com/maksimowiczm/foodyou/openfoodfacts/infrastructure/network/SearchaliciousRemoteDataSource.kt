package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.RateLimiter
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.SearchaliciousResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.userAgent
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Remote data source for the Searchalicious API.
 *
 * Searchalicious is a search service built on top of Elasticsearch that provides fast and
 * comprehensive product searches for Open Food Facts.
 */
internal class SearchaliciousRemoteDataSource(
    private val client: HttpClient,
    private val rateLimiter: RateLimiter,
    private val networkConfig: NetworkConfig,
    logger: Logger,
) {
    private val logger = logger.withTag(TAG)

    /**
     * Search for products using the Searchalicious API.
     *
     * @param query The search query, supports Lucene query syntax. Optional if sortBy is provided.
     * @param pageSize Number of results to return per page. Defaults to 10.
     * @param page Page number to request, starts at 1. Defaults to 1.
     * @param sortBy Field name to sort results by. Prefix with "-" for descending order.
     * @param ingredientsAnalysisTag When non-null, restricts results to products whose
     *   `ingredients_analysis_tags` field contains this value (e.g. `"en:vegan"`). Appended to
     *   the Lucene query using an AND clause so it composes safely with any text query.
     * @return SearchaliciousResponse containing the search results with raw JSON populated for each
     *   hit
     */
    suspend fun search(
        query: String? = null,
        pageSize: Int = 10,
        page: Int = 1,
        sortBy: String? = null,
        ingredientsAnalysisTag: String? = null,
    ): SearchaliciousResponse {
        if (!rateLimiter.canMakeRequest()) {
            logger.d { "Rate limit exceeded for OpenFoodFacts API" }
            throw OpenFoodFactsApiError.RateLimitExceeded()
        }

        return try {
            client
                .get("$API_URL/search") {
                    userAgent(networkConfig.userAgent)
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }

                    // Combine text query with optional dietary tag filter
                    val effectiveQuery = buildEffectiveQuery(query, ingredientsAnalysisTag)
                    effectiveQuery?.let { parameter("q", it) }
                    parameter("page_size", pageSize)
                    parameter("page", page)
                    sortBy?.let { parameter("sort_by", it) }
                }
                .body<SearchaliciousResponse>()
        } finally {
            rateLimiter.recordRequest()
        }
    }

    /**
     * Builds the final Lucene query string by ANDing the user text query with an optional
     * `ingredients_analysis_tags` filter clause.
     */
    private fun buildEffectiveQuery(query: String?, ingredientsAnalysisTag: String?): String? {
        val tagClause = ingredientsAnalysisTag?.let { """ingredients_analysis_tags:"$it"""" }
        return when {
            query != null && tagClause != null -> "($query) AND $tagClause"
            query != null -> query
            tagClause != null -> tagClause
            else -> null
        }
    }

    companion object {
        private const val API_URL = "https://search.openfoodfacts.org"
        private const val TAG = "SearchaliciousRemoteDataSource"
        private const val TIMEOUT = 60_000L

        fun rateLimiter(clock: Clock) = RateLimiter(clock, 20, 1.minutes)
    }
}
