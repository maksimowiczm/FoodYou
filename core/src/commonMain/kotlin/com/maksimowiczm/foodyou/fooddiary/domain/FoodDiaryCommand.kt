package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant

sealed interface FoodDiaryCommand {
    data class Create(
        val id: FoodDiaryEntryId,
        val profileIds: Set<ProfileId>,
        val snapshot: MeasuredFoodSnapshot,
        val mealId: MealId,
        val entryTimestamp: Instant,
        val timestamp: Instant,
    ) : FoodDiaryCommand

    data class Update(
        val timestamp: Instant,
        val transform: (FoodDiaryEntry) -> FoodDiaryEntry?,
    ) : FoodDiaryCommand

    data class Remove(
        val strategy: DeleteStrategy,
        val timestamp: Instant,
    ) : FoodDiaryCommand

    data class UnlinkFromMeal(val timestamp: Instant) : FoodDiaryCommand
}
