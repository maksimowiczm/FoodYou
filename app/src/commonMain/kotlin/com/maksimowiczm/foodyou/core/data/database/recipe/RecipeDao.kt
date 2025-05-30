package com.maksimowiczm.foodyou.core.data.database.recipe

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.maksimowiczm.foodyou.core.data.database.food.MeasurementSuggestionView
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

    @Delete
    protected abstract suspend fun deleteRecipeIngredient(
        recipeIngredientEntity: RecipeIngredientEntity
    )

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

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientProductDetails ri ON r.id = ri.r_recipeId
        WHERE r.id = :id
        """
    )
    abstract override fun observeRecipe(id: Long): Flow<RecipeWithIngredients?>

    @Transaction
    @Query(
        """
        SELECT r.*
        FROM RecipeEntity r
        LEFT JOIN RecipeIngredientProductDetails ri ON r.id = ri.r_recipeId
        WHERE r.id = :id
        """
    )
    abstract override suspend fun getRecipe(id: Long): RecipeWithIngredients?

    @Query(
        """
        SELECT *
        FROM MeasurementSuggestionView s
        WHERE
            (:query IS NULL OR s.name LIKE '%' || :query || '%' OR s.brand LIKE '%' || :query || '%')
             AND (:barcode IS NULL OR s.barcode = :barcode)
        """
    )
    abstract override fun queryIngredientsSuggestions(
        query: String?,
        barcode: String?
    ): PagingSource<Int, MeasurementSuggestionView>
}
