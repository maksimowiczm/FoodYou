package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryAnonymizedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntrySnapshotChangedEntryEvent

class FoodDiarySnapshotSynchronizer(private val repository: FoodDiaryEntryCompositionRepository) :
    EventHandler<FoodDiaryEntryEvent> {
    override suspend fun handle(event: FoodDiaryEntryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEntryEvent ->
                repository.saveReferences(
                    event.diaryEntryId,
                    event.snapshot.allIdentities.filterIsInstance<FoodSnapshotId.Tracked>().toSet(),
                )

            is FoodDiaryEntrySnapshotChangedEntryEvent ->
                repository.saveReferences(
                    event.diaryEntryId,
                    event.snapshot.allIdentities.filterIsInstance<FoodSnapshotId.Tracked>().toSet(),
                )

            is FoodDiaryEntryAnonymizedEntryEvent -> repository.removeReferences(event.diaryEntryId)

            is FoodDiaryEntryDeletedEntryEvent -> repository.removeReferences(event.diaryEntryId)

            else -> Unit
        }
    }
}
