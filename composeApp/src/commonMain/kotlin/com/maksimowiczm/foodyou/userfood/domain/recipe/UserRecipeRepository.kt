package com.maksimowiczm.foodyou.userfood.domain.recipe

import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow

interface UserRecipeRepository {
    suspend fun save(recipe: UserRecipe): Result<Unit, CircularUserRecipeReferenceError>

    fun observe(identity: UserRecipeIdentity): Flow<UserRecipe?>

    suspend fun delete(identity: UserRecipeIdentity)

    /**
     * Finds all recipes that use the specified food reference.
     *
     * @param foodReference The food reference to search for
     * @return List of recipes containing the specified food
     */
    suspend fun findRecipesUsingFood(foodReference: FoodReference): List<UserRecipe>
}
