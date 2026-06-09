package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent

class NestedRecipeSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent -> {
                // No action needed
            }

            is UserRecipeUpdatedEvent ->
                userRecipeService.updateRecipesWithRecipe(
                    identity = FoodCompositionComponentIdentity.Recipe(event.recipe.identity.id),
                    name = event.recipe.name,
                    composition = event.recipe.composition,
                    servingWeight = event.recipe.servingWeight,
                    image = event.recipe.image?.let(FoodCompositionComponentImage::Blob),
                )

            is UserRecipeDeletedEvent -> {
                userRecipeService.removeComponentFromRecipes(
                    identity = FoodCompositionComponentIdentity.Recipe(event.identity.id)
                )
            }
        }
    }
}
