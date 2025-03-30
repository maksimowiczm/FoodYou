package com.maksimowiczm.foodyou.feature.diary.data

import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.feature.diary.data.model.Recipe
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(database: DiaryDatabase) : RecipeRepository {
    override fun observeRecipeById(id: Long): Flow<Recipe?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(id: Long): Result<Unit, RecipeDeletionError> {
        TODO("Not yet implemented")
    }
}
