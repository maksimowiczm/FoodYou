package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryAnonymizedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealLinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealUnlinkedEntryEvent

class FoodDiaryMealSynchronizer(private val repository: FoodDiaryEntryMealRepository) :
    EventHandler<FoodDiaryEntryEvent> {
    override suspend fun handle(event: FoodDiaryEntryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEntryEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is FoodDiaryEntryMealLinkedEntryEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is FoodDiaryEntryMealUnlinkedEntryEvent ->
                repository.removeReference(event.diaryEntryId)

            is FoodDiaryEntryAnonymizedEntryEvent -> repository.removeReference(event.diaryEntryId)

            is FoodDiaryEntryDeletedEntryEvent -> repository.removeReference(event.diaryEntryId)

            else -> Unit
        }
    }
}
