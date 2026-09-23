package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId

internal class RoomFoodDiaryCompositionRepository(private val dao: FoodDiaryCompositionDao) :
    FoodDiaryCompositionRepository {
    override suspend fun findEntriesUsing(id: FoodSnapshotId.Tracked): List<FoodDiaryEntryId> {
        return dao.findEntriesBySnapshotId(id).map(::FoodDiaryEntryId)
    }

    override suspend fun saveReferences(
        id: FoodDiaryEntryId,
        trackedIds: Set<FoodSnapshotId.Tracked>,
    ) {
        val references = trackedIds.map { snapshotId ->
            FoodDiaryEntryReferenceEntity(
                entryId = id.id,
                snapshotId = snapshotId,
            )
        }
        dao.updateReferences(id.id, references)
    }

    override suspend fun removeReferences(id: FoodDiaryEntryId) {
        dao.deleteByEntryId(id.id)
    }
}
