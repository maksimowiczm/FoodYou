package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId

sealed interface SearchParameters {
    val query: SearchQuery

    /** Search in local database */
    data class User(
        override val query: SearchQuery,
        val accountId: LocalAccountId,
        val profileId: ProfileId,
        val orderBy: OrderBy,
    ) : SearchParameters {
        enum class OrderBy {
            NameAscending
        }
    }

    /** Search in Open Food Facts API */
    data class OpenFoodFacts(override val query: SearchQuery, val orderBy: OrderBy) :
        SearchParameters {
        enum class OrderBy {
            NameAscending
        }
    }

    /** Search in FoodData Central API */
    data class FoodDataCentral(override val query: SearchQuery, val orderBy: OrderBy) :
        SearchParameters {
        enum class OrderBy {
            NameAscending
        }
    }
}
