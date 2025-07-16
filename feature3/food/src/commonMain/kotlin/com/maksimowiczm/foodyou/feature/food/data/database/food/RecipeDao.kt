package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao {

    @Query("SELECT * FROM Recipe WHERE id = :recipeId")
    abstract fun observe(recipeId: Int): Flow<Recipe?>

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM Recipe r
        INNER JOIN RecipeIngredient ri ON r.id = ri.recipeId
        WHERE r.id = :recipeId
        """
    )
    abstract fun observeWithIngredients(recipeId: Int): Flow<RecipeWithIngredients?>

    @Insert
    protected abstract suspend fun insertRecipe(recipe: Recipe): Long

    @Insert
    protected abstract suspend fun insertRecipeIngredient(ingredient: RecipeIngredient): Long

    @Transaction
    open suspend fun insertRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>
    ): Long {
        val recipeId = insertRecipe(recipe)

        ingredients.forEach { ingredient ->
            val recipeIngredient = ingredient.copy(recipeId = recipeId)
            insertRecipeIngredient(recipeIngredient)
        }

        return recipeId
    }

    @Update
    protected abstract suspend fun updateRecipeEntity(recipeEntity: Recipe)

    @Query("DELETE FROM RecipeIngredient WHERE recipeId = :recipeId")
    protected abstract suspend fun deleteRecipeIngredientsByRecipeId(recipeId: Long)

    // Deletes all ingredients for the recipe and then inserts new ones
    @Transaction
    open suspend fun updateRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>
    ) {
        updateRecipeEntity(recipe)
        deleteRecipeIngredientsByRecipeId(recipe.id)

        ingredients.forEach { ingredient ->
            val recipeIngredientEntity = ingredient.copy(recipeId = recipe.id)
            insertRecipeIngredient(recipeIngredientEntity)
        }
    }

    @Delete
    abstract suspend fun delete(recipe: Recipe)
}
