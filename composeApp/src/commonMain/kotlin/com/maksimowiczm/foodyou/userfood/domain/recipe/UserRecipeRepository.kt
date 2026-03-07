package com.maksimowiczm.foodyou.userfood.domain.recipe

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import kotlinx.coroutines.flow.Flow

interface UserRecipeRepository {
    suspend fun create(
        accountId: LocalAccountId,
        name: UserRecipeName,
        servings: Double,
        image: Image.Local?,
        note: UserFoodNote?,
        finalWeight: Double?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<UserRecipeIdentity, CircularUserRecipeReferenceError>

    suspend fun update(
        identity: UserRecipeIdentity,
        name: UserRecipeName,
        servings: Double,
        image: Image.Local?,
        note: UserFoodNote?,
        finalWeight: Double?,
        ingredients: List<UserRecipeIngredient>,
    ): Result<Unit, CircularUserRecipeReferenceError>

    fun observe(identity: UserRecipeIdentity): Flow<UserRecipe?>

    suspend fun delete(identity: UserRecipeIdentity)

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
    ): List<UserRecipe>
}
