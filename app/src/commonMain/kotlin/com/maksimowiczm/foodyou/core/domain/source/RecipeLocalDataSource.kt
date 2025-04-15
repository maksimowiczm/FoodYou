package com.maksimowiczm.foodyou.core.domain.source

import androidx.paging.PagingSource
import com.maksimowiczm.foodyou.core.data.model.recipe.IngredientSuggestion
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeLocalDataSource {
    fun observeIngredientSuggestions(query: String?): PagingSource<Int, IngredientSuggestion>
    suspend fun insertRecipeWithIngredients(
        recipeEntity: RecipeEntity,
        recipeIngredientEntities: List<RecipeIngredientEntity>
    ): Long

    suspend fun deleteRecipe(recipeId: Long)
    suspend fun updateRecipeWithIngredients(
        recipeEntity: RecipeEntity,
        recipeIngredientEntities: List<RecipeIngredientEntity>
    )

    fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>
    suspend fun getRecipe(id: Long): RecipeWithIngredients?
}
