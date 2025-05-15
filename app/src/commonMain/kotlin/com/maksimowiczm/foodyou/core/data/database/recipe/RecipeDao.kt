package com.maksimowiczm.foodyou.core.data.database.recipe

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.core.data.model.recipe.IngredientSuggestion
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao : RecipeLocalDataSource {
    @Query(
        """
        SELECT 
            p.*,
            CASE 
                WHEN pm.measurement IS NOT NULL THEN pm.measurement
                WHEN p.servingWeight IS NOT NULL THEN 2
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 0
            END AS measurement,
            CASE 
                WHEN pm.quantity IS NOT NULL THEN pm.quantity
                WHEN p.servingWeight IS NOT NULL THEN 1
                WHEN p.packageWeight IS NOT NULL THEN 1
                ELSE 100
            END AS quantity
        FROM ProductEntity AS p
        LEFT JOIN (
            SELECT * FROM ProductMeasurementEntity
            WHERE createdAt IN (
                SELECT MAX(createdAt)
                FROM ProductMeasurementEntity
                GROUP BY productId
            )
        ) AS pm ON p.id = pm.productId
        WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
        ORDER BY p.name
        """
    )
    abstract override fun observeIngredientSuggestions(
        query: String?
    ): PagingSource<Int, IngredientSuggestion>

    @Insert
    protected abstract suspend fun insertRecipe(recipeEntity: RecipeEntity): Long

    @Insert
    protected abstract suspend fun insertRecipeIngredient(
        recipeIngredientEntity: RecipeIngredientEntity
    ): Long

    @Transaction
    override suspend fun insertRecipeWithIngredients(
        recipeEntity: RecipeEntity,
        recipeIngredientEntities: List<RecipeIngredientEntity>
    ): Long {
        val recipeId = insertRecipe(recipeEntity)
        recipeIngredientEntities.forEach {
            insertRecipeIngredient(
                it.copy(
                    recipeId = recipeId
                )
            )
        }

        return recipeId
    }

    @Suppress("FunctionName")
    @Delete
    protected abstract suspend fun _deleteRecipe(recipeEntity: RecipeEntity)

    @Delete
    protected abstract suspend fun deleteRecipeIngredient(
        recipeIngredientEntity: RecipeIngredientEntity
    )

    @Query(
        """
        DELETE FROM RecipeIngredientEntity
        WHERE recipeId = :recipeId
        """
    )
    protected abstract suspend fun deleteRecipeIngredients(recipeId: Long)

    @Transaction
    override suspend fun deleteRecipe(recipeId: Long) {
        val recipeWithIngredients = getRecipe(recipeId)
        if (recipeWithIngredients != null) {
            _deleteRecipe(recipeWithIngredients.recipeEntity)
            recipeWithIngredients.ingredients.forEach {
                deleteRecipeIngredient(it.recipeIngredientEntity)
            }
        }
    }

    @Update
    protected abstract suspend fun updateRecipe(recipeEntity: RecipeEntity)

    @Transaction
    override suspend fun updateRecipeWithIngredients(
        recipeEntity: RecipeEntity,
        recipeIngredientEntities: List<RecipeIngredientEntity>
    ) {
        updateRecipe(recipeEntity)
        deleteRecipeIngredients(recipeEntity.id)
        recipeIngredientEntities.forEach {
            insertRecipeIngredient(it.copy(recipeId = recipeEntity.id))
        }
    }

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        JOIN RecipeIngredientProductDetails ri ON r.id = ri.r_recipeId
        WHERE r.id = :id
        """
    )
    abstract override fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        JOIN RecipeIngredientProductDetails ri ON r.id = ri.r_recipeId
        WHERE r.id = :id
        """
    )
    abstract override suspend fun getRecipe(id: Long): RecipeWithIngredients?
}
