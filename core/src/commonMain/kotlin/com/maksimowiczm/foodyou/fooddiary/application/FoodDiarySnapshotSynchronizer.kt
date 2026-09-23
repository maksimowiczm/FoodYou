package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryAnonymizedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntrySnapshotChangedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent

class FoodDiarySnapshotSynchronizer(private val repository: FoodDiaryCompositionRepository) :
    EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent ->
                repository.saveReferences(
                    event.diaryEntryId,
                    event.snapshot.allIdentities.filterIsInstance<FoodSnapshotId.Tracked>().toSet(),
                )

            is FoodDiaryEntrySnapshotChangedEvent ->
                repository.saveReferences(
                    event.diaryEntryId,
                    event.snapshot.allIdentities.filterIsInstance<FoodSnapshotId.Tracked>().toSet(),
                )

            is FoodDiaryEntryAnonymizedEvent -> repository.removeReferences(event.diaryEntryId)

            is FoodDiaryEntryDeletedEvent -> repository.removeReferences(event.diaryEntryId)

            else -> Unit
        }
    }
}
