package com.maksimowiczm.foodyou.feature.diary.database.recipe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query(
        """
        SELECT *
        FROM RecipeEntity
        WHERE id = :id
        """
    )
    fun getRecipeById(id: Long): RecipeEntity?

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
        WHERE r.id = :id
        """
    )
    fun observeRecipeById(id: Long): Flow<RecipeWithIngredients?>

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert
    suspend fun insertRecipeIngredient(ingredient: RecipeIngredientEntity): Long

    @Transaction
    suspend fun insertRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>
    ): Long {
        val recipeId = insertRecipe(recipe)
        val ingredients = ingredients.map {
            it.copy(
                recipeId = recipeId
            )
        }
        ingredients.forEach {
            insertRecipeIngredient(it)
        }

        return recipeId
    }
}
