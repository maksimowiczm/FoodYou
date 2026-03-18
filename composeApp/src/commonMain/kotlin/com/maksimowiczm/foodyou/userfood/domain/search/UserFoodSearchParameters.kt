package com.maksimowiczm.foodyou.userfood.domain.search

import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery

data class UserFoodSearchParameters(val query: SearchQuery, val orderBy: OrderBy) {
    enum class OrderBy {
        /** Sort alphabetically by food name (A-Z). */
        NameAscending
    }
}
