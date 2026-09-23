package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery

/**
 * Search parameters for FoodData Central API.
 *
 * Searches the USDA FoodData Central database.
 *
 * @property query The search query string
 * @property orderBy Result ordering preference
 */
data class FoodDataCentralSearchParameters(
    val query: SearchQuery,
    val orderBy: OrderBy,
    val dataTypes: Set<DataType>?,
) {
    /** Ordering options for FoodData Central search results. */
    enum class OrderBy {
        /** Sort alphabetically by food name (A-Z). */
        NameAscending
    }

    enum class DataType(val filter: String) {
        Branded("Branded"),
        Foundation("Foundation"),
        Survey("Survey (FNDDS)"),
        SRLegacy("SR Legacy"),
    }
}
