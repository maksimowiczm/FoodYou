package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

interface FoodProductRepository {
    /** Loads a food product by its identity. */
    fun observe(queryParameters: QueryParameters): Flow<FoodStatus<FoodProductDto>>

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
}
