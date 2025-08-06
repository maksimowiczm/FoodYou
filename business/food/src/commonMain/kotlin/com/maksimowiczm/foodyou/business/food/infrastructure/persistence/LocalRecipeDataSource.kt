package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import kotlinx.coroutines.flow.Flow

internal interface LocalRecipeDataSource {
    fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?>

    suspend fun deleteRecipe(recipe: Recipe)

    suspend fun insertRecipe(recipe: Recipe): FoodId.Recipe

    suspend fun updateRecipe(recipe: Recipe)
}
