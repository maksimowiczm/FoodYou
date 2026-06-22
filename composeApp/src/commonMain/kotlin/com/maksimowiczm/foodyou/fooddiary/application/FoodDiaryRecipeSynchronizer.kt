package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent

class FoodDiaryRecipeSynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent -> Unit

            is UserRecipeUpdatedEvent ->
                foodDiaryService.updateEntriesWithRecipe(
                    identity = FoodCompositionComponentIdentity.Recipe(event.recipe.identity.id),
                    name = event.recipe.name,
                    components = event.recipe.components,
                    servingWeight = event.recipe.servingWeight,
                    packageWeight = event.recipe.totalWeight,
                    image = event.recipe.image?.let(FoodCompositionComponentImage::Blob),
                )

            is UserRecipeDeletedEvent -> {
                val identity = FoodCompositionComponentIdentity.Recipe(event.identity.id)
                when (event.strategy) {
                    DeleteStrategy.Delete -> foodDiaryService.removeComponentFromEntries(identity)
                    DeleteStrategy.Unlink -> foodDiaryService.unlinkComponentFromEntries(identity)
                }
            }
        }
    }
}
