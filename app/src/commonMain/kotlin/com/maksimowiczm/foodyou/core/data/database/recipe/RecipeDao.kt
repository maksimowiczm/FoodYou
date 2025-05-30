package com.maksimowiczm.foodyou.core.data.database.recipe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao : RecipeLocalDataSource {

    @Suppress("FunctionName")
    @Delete
    protected abstract suspend fun _deleteRecipe(recipeEntity: RecipeEntity)

    @Transaction
    override suspend fun deleteRecipe(recipeId: Long) {
        val recipeWithIngredients = getRecipe(recipeId)
        if (recipeWithIngredients != null) {
            _deleteRecipe(recipeWithIngredients.recipeEntity)
        }
    }

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientEntity i ON r.id = i.recipeId
        WHERE r.id = :id
        """
    )
    abstract override fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientEntity i ON r.id = i.recipeId
        WHERE r.id = :id
        """
    )
    abstract override suspend fun getRecipe(id: Long): RecipeWithIngredients?

    @Insert
    protected abstract suspend fun createRecipeEntity(recipeEntity: RecipeEntity): Long

    @Insert
    protected abstract suspend fun createRecipeIngredientEntity(
        ingredient: RecipeIngredientEntity
    ): Long

    @Transaction
    override suspend fun createRecipeWithIngredients(
        name: String,
        servings: Int,
        ingredients: List<RecipeIngredientEntity>
    ): Long {
        val recipeEntity = RecipeEntity(
            name = name,
            servings = servings
        )

        val recipeId = createRecipeEntity(recipeEntity)

        ingredients.forEach { ingredient ->
            val recipeIngredientEntity = ingredient.copy(recipeId = recipeId)
            createRecipeIngredientEntity(recipeIngredientEntity)
        }

        return recipeId
    }
}
