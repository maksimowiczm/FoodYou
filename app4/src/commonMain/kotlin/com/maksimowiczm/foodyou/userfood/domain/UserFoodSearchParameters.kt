package com.maksimowiczm.foodyou.userfood.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery

/**
 * Search parameters for local user database.
 *
 * Searches food products created or saved by a specific user profile.
 *
 * @property query The search query string
 * @property accountId The account identifier to search within
 * @property profileId The profile identifier to search within
 * @property orderBy Result ordering preference
 */
data class UserFoodSearchParameters(
    val query: SearchQuery,
    val accountId: LocalAccountId,
    val profileId: ProfileId,
    val orderBy: OrderBy,
) {
    /** Ordering options for user database search results. */
    enum class OrderBy {
        /** Sort alphabetically by food name (A-Z). */
        NameAscending
    }
}
