package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeLocalDataSource {

    @Query("DELETE FROM RecipeEntity WHERE id = :id")
    abstract suspend fun deleteRecipeById(id: Long)

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientEntity i ON r.id = i.recipeId
        WHERE r.id = :id
        """
    )
    abstract fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    @Insert
    protected abstract suspend fun createRecipeEntity(recipeEntity: RecipeEntity): Long

    @Insert
    protected abstract suspend fun createRecipeIngredientEntity(
        ingredient: RecipeIngredientEntity
    ): Long

    @Transaction
    open suspend fun createRecipeWithIngredients(
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<RecipeIngredientEntity>,
        note: String
    ): Long {
        val recipeEntity = RecipeEntity(
            name = name,
            servings = servings,
            isLiquid = isLiquid,
            note = note.trim().takeIf { it.isNotEmpty() }
        )

        val recipeId = createRecipeEntity(recipeEntity)

        ingredients.forEach { ingredient ->
            val recipeIngredientEntity = ingredient.copy(recipeId = recipeId)
            createRecipeIngredientEntity(recipeIngredientEntity)
        }

        return recipeId
    }

    @Update
    protected abstract suspend fun updateRecipeEntity(recipeEntity: RecipeEntity)

    @Query("DELETE FROM RecipeIngredientEntity WHERE recipeId = :recipeId")
    protected abstract suspend fun deleteRecipeIngredients(recipeId: Long)

    // Deletes all ingredients for the recipe and then inserts new ones
    @Transaction
    open suspend fun updateRecipeWithIngredients(
        recipeId: Long,
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<RecipeIngredientEntity>,
        note: String?
    ) {
        val recipeEntity = RecipeEntity(
            id = recipeId,
            name = name,
            servings = servings,
            isLiquid = isLiquid,
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )

        updateRecipeEntity(recipeEntity)

        deleteRecipeIngredients(recipeId)

        ingredients.forEach { ingredient ->
            val recipeIngredientEntity = ingredient.copy(recipeId = recipeId)
            createRecipeIngredientEntity(recipeIngredientEntity)
        }
    }
}
