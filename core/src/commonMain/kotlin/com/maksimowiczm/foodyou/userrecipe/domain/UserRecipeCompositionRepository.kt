package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import kotlinx.coroutines.flow.Flow

/**
 * Repository for tracking which user products and sub-recipes are used in user recipes.
 *
 * This acts as a reverse index to quickly find which recipes need to be updated when a product or
 * sub-recipe changes.
 */
interface UserRecipeCompositionRepository {

    /**
     * Finds all recipes that contain the given snapshot id in their composition.
     *
     * @param id The id of the product or recipe being searched for.
     * @return A list of identities for recipes that use the specified component.
     */
    suspend fun findRecipesUsing(id: FoodSnapshotId.Tracked): List<UserRecipeId>

    /**
     * Finds all recipes that (recursively) contain the given recipe id.
     *
     * @param id The id of the recipe.
     * @return A flow of sets of identities for recipes that (directly or indirectly) use the
     *   specified recipe.
     */
    fun observeAncestors(id: UserRecipeId): Flow<Set<UserRecipeId>>

    /**
     * Saves the association between a recipe and its component identities.
     *
     * @param id The id of the recipe.
     * @param trackedIds The set of all component identities used in the recipe.
     */
    suspend fun saveReferences(
        id: UserRecipeId,
        trackedIds: Set<FoodSnapshotId.Tracked>,
    )

    /**
     * Removes all recorded component associations for a specific recipe.
     *
     * @param id The id of the recipe to clear references for.
     */
    suspend fun removeReferences(id: UserRecipeId)
}
