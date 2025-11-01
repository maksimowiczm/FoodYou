package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

interface FoodProductRepository {
    /** Loads a food product by its identity. */
    suspend fun observe(queryParameters: QueryParameters): Flow<FoodStatus>

    /**
     * Refreshes a food product by its identity, fetching the latest data from the remote source.
     */
    suspend fun refresh(
        identity: FoodProductIdentity.OpenFoodFacts
    ): Result<FoodProductDto, FoodDatabaseError>

    /**
     * Refreshes a food product by its identity, fetching the latest data from the remote source.
     */
    suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError>

    /** Deletes a local food product by its identity. */
    suspend fun delete(identity: FoodProductIdentity.Local)

    sealed interface FoodStatus {
        /**
         * Indicates that the food product is currently being loaded. If partial data is available,
         * it is included.
         */
        data class Loading(val food: FoodProductDto?) : FoodStatus

        data object NotFound : FoodStatus

        data class Available(val food: FoodProductDto) : FoodStatus

        /**
         * Indicates that an error occurred while loading the food product. If partial data is
         * available, it is included.
         */
        data class Error(val food: FoodProductDto?, val error: FoodDatabaseError) : FoodStatus
    }
}
