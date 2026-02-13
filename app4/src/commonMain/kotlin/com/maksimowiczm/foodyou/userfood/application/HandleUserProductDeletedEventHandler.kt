package com.maksimowiczm.foodyou.userfood.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository

/**
 * Handles UserProductDeletedEvent by removing the deleted ingredient from affected recipes.
 *
 * When a user food product is deleted, this handler finds all recipes containing that ingredient
 * and removes it from the ingredient list. If removing the ingredient would leave the recipe with
 * no ingredients (violating business rules), the entire recipe is deleted instead.
 *
 * This approach:
 * - Preserves recipes when possible (removes only the deleted ingredient)
 * - Maintains data integrity (no invalid ingredient references)
 * - Respects business rule (recipe must have at least one ingredient)
 */
internal class HandleUserProductDeletedEventHandler(
    private val userRecipeRepository: UserRecipeRepository
) : EventHandler<UserProductDeletedEvent> {
    override suspend fun handle(event: UserProductDeletedEvent) {
        val foodReference = FoodReference.UserProduct(event.identity.id)
        val accountId = event.identity.accountId

        userRecipeRepository.findRecipesUsingFood(foodReference, accountId).forEach { recipe ->
            val updatedIngredients = recipe.ingredients.filter { it.foodReference != foodReference }

            // If no ingredients left, delete the recipe (business rule violation)
            if (updatedIngredients.isEmpty()) {
                userRecipeRepository.delete(recipe.identity)
            } else {
                userRecipeRepository.update(
                    identity = recipe.identity,
                    name = recipe.name,
                    servings = recipe.servings,
                    image = recipe.image,
                    note = recipe.note,
                    finalWeight = recipe.finalWeight,
                    ingredients = updatedIngredients,
                )
            }
        }
    }
}
