package com.maksimowiczm.foodyou.userfood.domain.search

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery

data class UserFoodSearchParameters(
    val query: SearchQuery,
    val localAccountId: LocalAccountId,
    val orderBy: OrderBy,
) {
    enum class OrderBy {
        /** Sort alphabetically by food name (A-Z). */
        NameAscending
    }
}
