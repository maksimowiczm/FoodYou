package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUnlinkedFromMealEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUpdatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository

class FoodDiaryMealSynchronizer(private val repository: FoodDiaryMealRepository) :
    EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent -> {
                repository.saveReference(event.diaryEntryId, event.mealId)
            }

            is FoodDiaryEntryUpdatedEvent -> {
                if (event.mealId != null) {
                    repository.saveReference(event.diaryEntryId, event.mealId)
                } else {
                    repository.removeReference(event.diaryEntryId)
                }
            }

            is FoodDiaryEntryDeletedEvent -> repository.removeReference(event.diaryEntryId)

            is FoodDiaryEntryUnlinkedFromMealEvent -> repository.removeReference(event.diaryEntryId)
        }
    }
}
