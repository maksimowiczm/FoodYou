package com.maksimowiczm.foodyou.feature.diary.data

import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.feature.diary.data.model.Recipe
import kotlinx.coroutines.flow.Flow

enum class RecipeCreationError {
    PRODUCT_ALREADY_EXISTS
}

enum class RecipeUpdateError {
    PRODUCT_NOT_FOUND
}

enum class RecipeDeletionError {
    PRODUCT_NOT_FOUND
}

interface RecipeRepository {
    fun observeRecipeById(id: Long): Flow<Recipe?>

    suspend fun deleteRecipe(id: Long): Result<Unit, RecipeDeletionError>
}
