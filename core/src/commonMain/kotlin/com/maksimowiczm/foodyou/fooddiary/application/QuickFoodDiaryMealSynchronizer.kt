package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryCreatedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryDeletedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryImportedFoodYou3Event
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealLinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealUnlinkedEntryEvent

class QuickFoodDiaryMealSynchronizer(private val repository: QuickFoodDiaryEntryMealRepository) :
    EventHandler<QuickFoodDiaryEntryEvent> {
    override suspend fun handle(event: QuickFoodDiaryEntryEvent) {
        when (event) {
            is QuickFoodDiaryEntryCreatedEntryEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is QuickFoodDiaryEntryImportedFoodYou3Event ->
                if (event.mealId != null) {
                    repository.saveReference(event.diaryEntryId, event.mealId)
                }

            is QuickFoodDiaryEntryMealLinkedEntryEvent ->
                repository.saveReference(event.diaryEntryId, event.mealId)

            is QuickFoodDiaryEntryMealUnlinkedEntryEvent ->
                repository.removeReference(event.diaryEntryId)

            is QuickFoodDiaryEntryDeletedEntryEvent ->
                repository.removeReference(event.diaryEntryId)

            else -> Unit
        }
    }
}
