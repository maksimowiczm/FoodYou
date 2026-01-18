package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId

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
     * Search parameters for local user database.
     *
     * Searches food products created or saved by a specific user profile.
     *
     * @property query The search query string
     * @property accountId The account identifier to search within
     * @property profileId The profile identifier to search within
     * @property orderBy Result ordering preference
     */
    data class User(
        override val query: SearchQuery,
        val accountId: LocalAccountId,
        val profileId: ProfileId,
        val orderBy: OrderBy,
    ) : SearchParameters {
        /** Ordering options for user database search results. */
        enum class OrderBy {
            /** Sort alphabetically by food name (A-Z). */
            NameAscending
        }
    }

    /**
     * Search parameters for Open Food Facts API.
     *
     * Searches the global Open Food Facts product database.
     *
     * @property query The search query string
     * @property orderBy Result ordering preference
     */
    data class OpenFoodFacts(override val query: SearchQuery, val orderBy: OrderBy) :
        SearchParameters {
        /** Ordering options for Open Food Facts search results. */
        enum class OrderBy {
            /** Sort alphabetically by product name (A-Z). */
            NameAscending
        }
    }

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
