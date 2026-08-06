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
                repository.saveReference(event.identity, event.mealIdentity)
            }

            is FoodDiaryEntryUpdatedEvent -> Unit

            is FoodDiaryEntryDeletedEvent -> repository.removeReference(event.identity)

            is FoodDiaryEntryUnlinkedFromMealEvent -> repository.removeReference(event.identity)
        }
    }
}
