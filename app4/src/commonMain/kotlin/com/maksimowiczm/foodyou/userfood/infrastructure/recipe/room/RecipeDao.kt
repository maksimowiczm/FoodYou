package com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class RecipeDao {
    @Insert protected abstract suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert
    protected abstract suspend fun insertIngredients(ingredients: List<RecipeIngredientEntity>)

    @Update protected abstract suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete abstract suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM RecipeIngredient WHERE recipeSqliteId = :recipeSqliteId")
    protected abstract suspend fun deleteIngredients(recipeSqliteId: Long)

    @Transaction
    @Query(
        """
        SELECT *
        FROM Recipe
        WHERE accountId = :accountId AND uuid = :uuid
        LIMIT 1
    """
    )
    abstract fun observe(uuid: String, accountId: String): Flow<RecipeWithIngredients?>

    @Transaction
    @Query(
        """
        SELECT *
        FROM Recipe
        WHERE accountId = :accountId
        ORDER BY name ASC
        """
    )
    abstract fun getPagingSource(accountId: String): PagingSource<Int, RecipeWithIngredients>

    @Query(
        """
        SELECT COUNT(*)
        FROM Recipe
        WHERE accountId = :accountId
        """
    )
    abstract fun observeCount(accountId: String): Flow<Int>

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM Recipe r JOIN RecipeFts fts ON r.sqliteId = fts.rowid
        WHERE 
            r.accountId = :accountId AND
            RecipeFts MATCH :query || '*'
        ORDER BY r.name ASC
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String,
        accountId: String,
    ): PagingSource<Int, RecipeWithIngredients>

    @Query(
        """
        SELECT COUNT(*)
        FROM Recipe r JOIN RecipeFts fts ON r.sqliteId = fts.rowid
        WHERE 
            r.accountId = :accountId AND
            RecipeFts MATCH :query || '*'
        """
    )
    abstract fun observeCountByQuery(query: String, accountId: String): Flow<Int>

    @Transaction
    @Query(
        """
        SELECT Recipe.*
        FROM Recipe
        INNER JOIN RecipeIngredient ON Recipe.sqliteId = RecipeIngredient.recipeSqliteId
        WHERE Recipe.accountId = :accountId 
            AND RecipeIngredient.foodReferenceType = :foodReferenceType
            AND RecipeIngredient.foodId = :foodId
        """
    )
    abstract suspend fun findRecipesUsingFood(
        accountId: String,
        foodReferenceType: FoodReferenceType,
        foodId: String,
    ): List<RecipeWithIngredients>

    /** Atomically insert a recipe with its ingredients in a single transaction. */
    @Transaction
    open suspend fun insertRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ): Long {
        val recipeSqliteId = insertRecipe(recipe)
        val ingredientsWithRecipeId = ingredients.map { it.copy(recipeSqliteId = recipeSqliteId) }
        insertIngredients(ingredientsWithRecipeId)
        return recipeSqliteId
    }

    /** Atomically update a recipe with its ingredients in a single transaction. */
    @Transaction
    open suspend fun updateRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ) {
        updateRecipe(recipe)
        deleteIngredients(recipe.sqliteId)
        val ingredientsWithRecipeId = ingredients.map { it.copy(recipeSqliteId = recipe.sqliteId) }
        insertIngredients(ingredientsWithRecipeId)
    }
}
