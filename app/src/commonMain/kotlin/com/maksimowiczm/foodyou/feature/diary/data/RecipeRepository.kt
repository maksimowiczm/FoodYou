package com.maksimowiczm.foodyou.feature.diary.data

import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Recipe
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow

enum class RecipeUpdateError {
    PRODUCT_NOT_FOUND
}

enum class RecipeDeletionError {
    PRODUCT_NOT_FOUND
}

interface RecipeRepository {
    fun observeRecipeById(id: Long): Flow<Recipe?>

    suspend fun createRecipe(
        name: String,
        servings: Int?,
        ingredients: List<Pair<FoodId.Product, WeightMeasurement>>
    ): FoodId.Recipe

    suspend fun deleteRecipe(id: Long): Result<Unit, RecipeDeletionError>
}
