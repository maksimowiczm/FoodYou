package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealLinkedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealUnlinkedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository

class FoodDiaryMealSynchronizer(private val repository: FoodDiaryMealRepository) :
    EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is FoodDiaryEntryMealLinkedEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is FoodDiaryEntryMealUnlinkedEvent -> repository.removeReference(event.diaryEntryId)

            is FoodDiaryEntryDeletedEvent -> repository.removeReference(event.diaryEntryId)

            else -> Unit
        }
    }
}
