package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery

/**
 * Search parameters for Open Food Facts API.
 *
 * Searches the global Open Food Facts product database.
 *
 * @property query The search query string
 * @property orderBy Result ordering preference
 * @property dietaryFilter Optional filter restricting results to products tagged with a specific
 *   dietary classification in the Open Food Facts `ingredients_analysis_tags` field
 */
data class OpenFoodFactsSearchParameters(
    val query: SearchQuery,
    val orderBy: OrderBy,
    val dietaryFilter: DietaryFilter? = null,
) {
    enum class OrderBy {
        Relevance
    }

    /**
     * Dietary classification filter backed by Open Food Facts `ingredients_analysis_tags`.
     *
     * Each entry corresponds to a tag value already present in the OFF database. When set, only
     * products whose `ingredients_analysis_tags` list includes the matching tag are returned.
     */
    enum class DietaryFilter(val tag: String) {
        Vegan("en:vegan"),
        Vegetarian("en:vegetarian"),
    }
}
