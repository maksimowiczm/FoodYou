package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing food product data from various sources.
 *
 * Handles loading, refreshing, and deleting food products from local database and external sources
 * (Open Food Facts, FoodData Central). Provides reactive updates through Flow for observing food
 * product state changes.
 */
interface FoodProductRepository {
    /**
     * Observes a food product by query parameters, emitting status updates.
     *
     * @param queryParameters The query parameters identifying the food product
     * @return Flow emitting FoodStatus updates as the product loads or changes
     */
    fun observe(identity: FoodProductIdentity): Flow<FoodStatus>

    /**
     * Refreshes a food product from FoodData Central, fetching the latest data.
     *
     * @param identity The FoodData Central identifier
     * @return Result containing the updated food product or an error
     */
    suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError>

    /**
     * Represents the current status of a food product query.
     *
     * Provides different states for tracking food product availability, loading progress, and
     * errors, with optional partial data.
     */
    sealed interface FoodStatus {
        /** The identity of the food product this status refers to. */
        val identity: FoodProductIdentity

        /**
         * Indicates that the food product is currently being loaded.
         *
         * @property identity The food product identifier
         * @property food Partial food data if available from cache
         */
        data class Loading(override val identity: FoodProductIdentity, val food: FoodProductDto?) :
            FoodStatus

        /**
         * Indicates that the food product was not found.
         *
         * @property identity The food product identifier that was not found
         */
        data class NotFound(override val identity: FoodProductIdentity) : FoodStatus

        /**
         * Indicates that the food product data is available.
         *
         * @property food The complete food product data
         */
        data class Available(val food: FoodProductDto) : FoodStatus {
            override val identity: FoodProductIdentity = food.identity
        }

        /**
         * Indicates that an error occurred while loading the food product.
         *
         * @property identity The food product identifier
         * @property food Partial food data if available from cache
         * @property error The error that occurred during loading
         */
        data class Error(
            override val identity: FoodProductIdentity,
            val food: FoodProductDto?,
            val error: FoodDatabaseError,
        ) : FoodStatus
    }
}
