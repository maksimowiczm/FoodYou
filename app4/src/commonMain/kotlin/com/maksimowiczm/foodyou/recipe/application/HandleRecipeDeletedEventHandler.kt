package com.maksimowiczm.foodyou.recipe.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.recipe.domain.FoodReference
import com.maksimowiczm.foodyou.recipe.domain.RecipeDeletedEvent
import com.maksimowiczm.foodyou.recipe.domain.RecipeRepository

/**
 * Handles RecipeDeletedEvent by removing the deleted recipe from other recipes that use it as an
 * ingredient.
 *
 * When a recipe is deleted, this handler finds all recipes that contain the deleted recipe as an
 * ingredient (nested recipes) and removes it from their ingredient list. If removing the ingredient
 * would leave a recipe with no ingredients (violating business rules), that recipe is deleted as
 * well (cascade deletion).
 *
 * This approach:
 * - Maintains referential integrity (no invalid recipe references)
 * - Preserves recipes when possible (removes only the deleted recipe ingredient)
 * - Respects business rule (recipe must have at least one ingredient)
 * - Handles cascading deletions (recipe A uses recipe B, which uses recipe C)
 */
internal class HandleRecipeDeletedEventHandler(private val recipeRepository: RecipeRepository) :
    EventHandler<RecipeDeletedEvent> {
    override suspend fun handle(event: RecipeDeletedEvent) {
        val foodReference = FoodReference.Recipe(event.identity.id)
        val accountId = event.identity.accountId

        recipeRepository.findRecipesUsingFood(foodReference, accountId).forEach { recipe ->
            val updatedIngredients = recipe.ingredients.filter { it.foodReference != foodReference }

            // If no ingredients left, delete the recipe (business rule violation)
            // This will trigger another RecipeDeletedEvent, causing cascade deletion
            if (updatedIngredients.isEmpty()) {
                recipeRepository.delete(recipe.identity)
            } else {
                recipeRepository.update(
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
