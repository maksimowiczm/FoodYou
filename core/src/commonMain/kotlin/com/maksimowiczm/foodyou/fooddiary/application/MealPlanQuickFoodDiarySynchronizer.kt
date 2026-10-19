package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.mealplan.domain.MealDeletedEvent

class MealPlanQuickFoodDiarySynchronizer(private val quickFoodDiaryService: QuickFoodDiaryService) :
    EventHandler<MealDeletedEvent> {
    override suspend fun handle(event: MealDeletedEvent) {
        quickFoodDiaryService.unlinkEntriesFromMeal(event.mealId)
    }
}
