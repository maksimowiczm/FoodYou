package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity

/**
 * Repository for tracking which user products and sub-recipes are used in user recipes.
 *
 * This acts as a reverse index to quickly find which recipes need to be updated when a product or
 * sub-recipe changes.
 */
interface UserRecipeCompositionRepository {

    /**
     * Finds all recipes that contain the given component identity in their composition.
     *
     * @param identity The identity of the product or recipe being searched for.
     * @return A list of identities for recipes that use the specified component.
     */
    suspend fun findRecipesUsing(
        identity: FoodCompositionComponentIdentity
    ): List<UserRecipeIdentity>

    /**
     * Saves the association between a recipe and its component identities.
     *
     * @param recipeIdentity The identity of the recipe.
     * @param identities The set of all component identities used in the recipe.
     */
    suspend fun saveReferences(
        recipeIdentity: UserRecipeIdentity,
        identities: Set<FoodCompositionComponentIdentity>,
    )

    /**
     * Removes all recorded component associations for a specific recipe.
     *
     * @param recipeIdentity The identity of the recipe to clear references for.
     */
    suspend fun removeReferences(recipeIdentity: UserRecipeIdentity)
}
