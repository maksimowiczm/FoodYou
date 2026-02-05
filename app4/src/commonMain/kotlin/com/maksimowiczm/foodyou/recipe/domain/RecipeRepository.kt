package com.maksimowiczm.foodyou.recipe.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.userfood.domain.FoodNote
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun search(parameters: RecipeSearchParameters, pageSize: Int): Flow<PagingData<Recipe>>

    fun count(parameters: RecipeSearchParameters): Flow<Int>

    suspend fun create(
        accountId: LocalAccountId,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    ): Result<RecipeIdentity, CircularRecipeReferenceError>

    suspend fun update(
        identity: RecipeIdentity,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    ): Result<Unit, CircularRecipeReferenceError>

    fun observe(identity: RecipeIdentity): Flow<Recipe?>

    suspend fun delete(identity: RecipeIdentity)

    /**
     * Finds all recipes that use the specified food reference.
     *
     * @param foodReference The food reference to search for
     * @param accountId The account to search within
     * @return List of recipes containing the specified food
     */
    suspend fun findRecipesUsingFood(
        foodReference: FoodReference,
        accountId: LocalAccountId,
    ): List<Recipe>
}
