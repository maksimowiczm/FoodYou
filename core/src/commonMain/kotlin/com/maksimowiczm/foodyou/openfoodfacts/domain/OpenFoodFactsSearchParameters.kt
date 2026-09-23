package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery

/**
 * Search parameters for Open Food Facts API.
 *
 * Searches the global Open Food Facts product database.
 *
 * @property query The search query string
 * @property orderBy Result ordering preference
 */
data class OpenFoodFactsSearchParameters(
    val query: SearchQuery,
    val orderBy: OrderBy,
    val version: OpenFoodFactsVersion,
) {
    enum class OrderBy {
        Relevance
    }

    enum class OpenFoodFactsVersion {
        ApiV1,
        SearchALicious,
    }
}
