package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant

sealed interface FoodDiaryEntryCommand {
    data class Create(
        val id: FoodDiaryEntryId,
        val profileIds: Set<ProfileId>,
        val snapshot: MeasuredFoodSnapshot,
        val mealId: MealId,
        val entryTimestamp: Instant,
    ) : FoodDiaryEntryCommand

    data class Update(val transform: (FoodDiaryEntry) -> FoodDiaryEntry?) : FoodDiaryEntryCommand

    data object Delete : FoodDiaryEntryCommand

    data object Anonymize : FoodDiaryEntryCommand

    data object UnlinkFromMeal : FoodDiaryEntryCommand
}
