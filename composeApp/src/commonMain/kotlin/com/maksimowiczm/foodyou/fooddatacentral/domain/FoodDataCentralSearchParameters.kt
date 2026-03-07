package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery

/**
 * Search parameters for FoodData Central API.
 *
 * Searches the USDA FoodData Central database.
 *
 * @property query The search query string
 * @property orderBy Result ordering preference
 */
data class FoodDataCentralSearchParameters(val query: SearchQuery, val orderBy: OrderBy) {
    /** Ordering options for FoodData Central search results. */
    enum class OrderBy {
        /** Sort alphabetically by food name (A-Z). */
        NameAscending
    }
}
