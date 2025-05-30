package com.maksimowiczm.foodyou.core.domain.source

import androidx.paging.PagingSource
import com.maksimowiczm.foodyou.core.data.database.food.MeasurementSuggestionView
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeLocalDataSource {

    suspend fun deleteRecipe(recipeId: Long)

    fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    suspend fun getRecipe(id: Long): RecipeWithIngredients?

    fun queryIngredientsSuggestions(
        query: String?,
        barcode: String?
    ): PagingSource<Int, MeasurementSuggestionView>
}
