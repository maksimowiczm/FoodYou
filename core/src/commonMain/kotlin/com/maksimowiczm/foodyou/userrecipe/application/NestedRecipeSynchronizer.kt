package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot

class NestedRecipeSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent -> {
                // No action needed
            }

            is UserRecipeUpdatedEvent ->
                userRecipeService.updateRecipesUsing(
                    snapshot = event.recipe.toSnapshot(),
                    servingWeight = event.recipe.servingWeight,
                    packageWeight = event.recipe.totalWeight,
                )

            is UserRecipeDeletedEvent -> {
                val id = FoodSnapshotId.UserRecipe(event.userRecipeId.value)
                when (event.strategy) {
                    DeleteStrategy.Delete -> userRecipeService.removeComponentFromRecipes(id)
                    DeleteStrategy.Unlink -> userRecipeService.unlinkComponentFromRecipes(id)
                }
            }
        }
    }
}
