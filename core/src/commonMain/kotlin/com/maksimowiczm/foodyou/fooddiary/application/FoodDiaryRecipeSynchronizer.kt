package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot

class FoodDiaryRecipeSynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent -> Unit

            is UserRecipeUpdatedEvent ->
                foodDiaryService.updateEntriesUsing(
                    snapshot = event.recipe.toSnapshot(),
                    servingWeight = event.recipe.servingWeight,
                    packageWeight = event.recipe.totalWeight,
                )

            is UserRecipeDeletedEvent -> {
                val identity = FoodSnapshotId.UserRecipe(event.identity.id)
                when (event.strategy) {
                    DeleteStrategy.Delete -> foodDiaryService.removeComponentFromEntries(identity)
                    DeleteStrategy.Unlink -> foodDiaryService.unlinkComponentFromEntries(identity)
                }
            }
        }
    }
}
