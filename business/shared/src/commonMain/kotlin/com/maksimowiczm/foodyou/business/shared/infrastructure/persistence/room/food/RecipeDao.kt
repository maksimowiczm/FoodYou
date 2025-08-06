package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

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
    abstract fun observeRecipe(recipeId: Long): Flow<RecipeEntity?>

    @Query("SELECT * FROM RecipeIngredient WHERE recipeId = :recipeId")
    abstract fun observeRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredientEntity>>

    @Insert protected abstract suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert
    protected abstract suspend fun insertRecipeIngredient(ingredient: RecipeIngredientEntity): Long

    @Transaction
    open suspend fun insertRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ): Long {
        val recipeId = insertRecipe(recipe)

        ingredients.forEach { ingredient ->
            val recipeIngredient = ingredient.copy(recipeId = recipeId)
            insertRecipeIngredient(recipeIngredient)
        }

        return recipeId
    }

    @Update protected abstract suspend fun updateRecipeEntity(recipeEntity: RecipeEntity)

    @Query("DELETE FROM RecipeIngredient WHERE recipeId = :recipeId")
    protected abstract suspend fun deleteRecipeIngredientsByRecipeId(recipeId: Long)

    // Deletes all ingredients for the recipe and then inserts new ones
    @Transaction
    open suspend fun updateRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ) {
        updateRecipeEntity(recipe)
        deleteRecipeIngredientsByRecipeId(recipe.id)

        ingredients.forEach { ingredient ->
            val recipeIngredientEntity = ingredient.copy(recipeId = recipe.id)
            insertRecipeIngredient(recipeIngredientEntity)
        }
    }

    @Delete abstract suspend fun delete(recipe: RecipeEntity)
}
