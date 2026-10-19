package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant

sealed interface QuickFoodDiaryEntryCommand {
    data class Create(
        val id: QuickFoodDiaryEntryId,
        val profileIds: Set<ProfileId>,
        val name: String,
        val nutritionFacts: NutritionFacts,
        val mealId: MealId,
        val entryTimestamp: Instant,
    ) : QuickFoodDiaryEntryCommand

    data class Update(val transform: (QuickFoodDiaryEntry) -> QuickFoodDiaryEntry?) :
        QuickFoodDiaryEntryCommand

    data class LinkToMeal(val mealId: MealId) : QuickFoodDiaryEntryCommand

    data object UnlinkFromMeal : QuickFoodDiaryEntryCommand

    data object Delete : QuickFoodDiaryEntryCommand
}
