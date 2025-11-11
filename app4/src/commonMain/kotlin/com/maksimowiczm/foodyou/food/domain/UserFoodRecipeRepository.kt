package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Quantity
import kotlinx.coroutines.flow.Flow

interface UserFoodRecipeRepository {
    suspend fun create(
        name: FoodName,
        note: FoodNote?,
        image: FoodImage?,
        source: FoodSource?,
        isLiquid: Boolean,
        servings: Int,
        ingredients: List<Pair<FoodIdentity, Quantity>>,
    ): LocalFoodRecipeIdentity

    suspend fun edit(
        identity: LocalFoodRecipeIdentity,
        name: FoodName,
        note: FoodNote?,
        image: FoodImage?,
        source: FoodSource?,
        isLiquid: Boolean,
        servings: Int,
        ingredients: List<Pair<FoodIdentity, Quantity>>,
    )

    // Recipe ingredients might be not downloaded. Use FoodStatus? Same as FoodProductRepository, or
    // use food status for each ingredient?
    fun observe(identity: LocalFoodRecipeIdentity): Flow<FoodRecipeDto?>

    suspend fun delete(identity: LocalFoodRecipeIdentity)
}
