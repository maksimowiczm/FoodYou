package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Quantity
import kotlinx.coroutines.flow.Flow

interface UserFoodRecipeRepository {
    suspend fun create(
        name: FoodName,
        note: FoodNote?,
        image: FoodImage?,
        source: FoodSource?,
        servings: Int,
        ingredients: List<Pair<FoodIdentity, Quantity>>,
    ): LocalFoodRecipeIdentity

    suspend fun edit(
        identity: LocalFoodRecipeIdentity,
        name: FoodName,
        note: FoodNote?,
        image: FoodImage?,
        source: FoodSource?,
        servings: Int,
        ingredients: List<Pair<FoodIdentity, Quantity>>,
    )

    fun observeLazy(identity: LocalFoodRecipeIdentity): Flow<LazyFoodRecipeDto?>

    fun observeSimple(identity: LocalFoodRecipeIdentity): Flow<FoodStatus<SimpleFoodRecipeDto>>

    suspend fun delete(identity: LocalFoodRecipeIdentity)
}
