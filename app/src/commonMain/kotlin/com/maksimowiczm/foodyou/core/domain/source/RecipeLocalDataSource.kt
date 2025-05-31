package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeLocalDataSource {

    suspend fun deleteRecipe(recipeId: Long)

    fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    suspend fun getRecipe(id: Long): RecipeWithIngredients?

    suspend fun createRecipeWithIngredients(
        name: String,
        servings: Int,
        ingredients: List<RecipeIngredientEntity>
    ): Long

    suspend fun updateRecipeWithIngredients(
        recipeId: Long,
        name: String,
        servings: Int,
        ingredients: List<RecipeIngredientEntity>
    )
}
