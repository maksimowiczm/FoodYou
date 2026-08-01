package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUnlinkedFromMealEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUpdatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent

class FoodDiaryCompositionSynchronizer(private val repository: FoodDiaryCompositionRepository) :
    EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent ->
                repository.saveReferences(
                    event.identity,
                    event.composition.allIdentities
                        .filterIsInstance<FoodCompositionComponentIdentity.Identified>()
                        .toSet(),
                )

            is FoodDiaryEntryUpdatedEvent ->
                repository.saveReferences(
                    event.identity,
                    event.composition.allIdentities
                        .filterIsInstance<FoodCompositionComponentIdentity.Identified>()
                        .toSet(),
                )

            is FoodDiaryEntryDeletedEvent -> repository.removeReferences(event.identity)

            is FoodDiaryEntryUnlinkedFromMealEvent -> Unit
        }
    }
}
