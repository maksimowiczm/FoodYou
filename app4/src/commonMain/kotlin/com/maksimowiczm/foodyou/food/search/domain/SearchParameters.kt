package com.maksimowiczm.foodyou.food.search.domain

sealed interface SearchParameters {
    val query: SearchQuery

    /** Search in local database */
    data class Local(override val query: SearchQuery) : SearchParameters

    /** Search in Open Food Facts API */
    data class OpenFoodFacts(override val query: SearchQuery, val orderBy: OrderBy) :
        SearchParameters {
        enum class OrderBy {
            NameAscending
        }
    }

    /** Search in FoodData Central API */
    data class FoodDataCentral(override val query: SearchQuery) : SearchParameters
}
