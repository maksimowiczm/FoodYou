package com.maksimowiczm.foodyou.food.search.domain

/**
 * Sealed interface representing search parameters for food products.
 *
 * Encapsulates different search contexts (local database, Open Food Facts, FoodData Central) with
 * their specific filtering and ordering requirements.
 */
sealed interface SearchParameters {
    /** The search query string. */
    val query: SearchQuery

    /**
     * Search parameters for FoodData Central API.
     *
     * Searches the USDA FoodData Central database.
     *
     * @property query The search query string
     * @property orderBy Result ordering preference
     */
    data class FoodDataCentral(override val query: SearchQuery, val orderBy: OrderBy) :
        SearchParameters {
        /** Ordering options for FoodData Central search results. */
        enum class OrderBy {
            /** Sort alphabetically by food name (A-Z). */
            NameAscending
        }
    }
}
