package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId

internal class RoomFoodDiaryEntryCompositionRepository(
    private val dao: FoodDiaryEntryCompositionDao
) : FoodDiaryEntryCompositionRepository {
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
