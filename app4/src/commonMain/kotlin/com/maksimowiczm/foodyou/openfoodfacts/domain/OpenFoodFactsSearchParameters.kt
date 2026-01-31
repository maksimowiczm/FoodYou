package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery

/**
 * Search parameters for Open Food Facts API.
 *
 * Searches the global Open Food Facts product database.
 *
 * @property query The search query string
 * @property orderBy Result ordering preference
 */
data class OpenFoodFactsSearchParameters(val query: SearchQuery, val orderBy: OrderBy) {
    /** Ordering options for Open Food Facts search results. */
    enum class OrderBy {
        /** Sort alphabetically by product name (A-Z). */
        NameAscending
    }
}
