package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.mealplan.domain.MealDeletedEvent

class MealPlanFoodDiarySynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<MealDeletedEvent> {
    override suspend fun handle(event: MealDeletedEvent) {
        foodDiaryService.unlinkEntriesFromMeal(event.identity)
    }
}
