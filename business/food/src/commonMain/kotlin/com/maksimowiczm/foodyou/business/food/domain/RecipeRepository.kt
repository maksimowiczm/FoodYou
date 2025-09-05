package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?>

    suspend fun insertRecipe(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<RecipeIngredient>,
    ): FoodId.Recipe

    suspend fun updateRecipe(recipe: Recipe)

    suspend fun deleteRecipe(recipe: Recipe)
}
