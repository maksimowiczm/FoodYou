package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity.FoodDataCentral
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity.OpenFoodFacts

interface FoodProductRepository {
    /** Loads a food product by its identity. */
    suspend fun load(identity: FoodProductIdentity): Result<FoodProductDto, FoodDatabaseError>

    /**
     * Refreshes a food product by its identity, fetching the latest data from the remote source.
     */
    suspend fun refresh(identity: OpenFoodFacts): Result<FoodProductDto, FoodDatabaseError>

    /**
     * Refreshes a food product by its identity, fetching the latest data from the remote source.
     */
    suspend fun refresh(identity: FoodDataCentral): Result<FoodProductDto, FoodDatabaseError>
}
