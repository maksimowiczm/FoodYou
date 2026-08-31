package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity

internal class RoomFoodDiaryCompositionRepository(private val dao: FoodDiaryCompositionDao) :
    FoodDiaryCompositionRepository {
    override suspend fun findEntriesUsing(
        identity: FoodSnapshotId.Tracked
    ): List<FoodDiaryEntryIdentity> {
        return dao.findEntriesBySnapshotId(identity).map(::FoodDiaryEntryIdentity)
    }

    override suspend fun saveReferences(
        identity: FoodDiaryEntryIdentity,
        identities: Set<FoodSnapshotId.Tracked>,
    ) {
        val references = identities.map { componentIdentity ->
            FoodDiaryEntryReferenceEntity(
                entryId = identity.id,
                snapshotId = componentIdentity,
            )
        }
        dao.updateReferences(identity.id, references)
    }

    override suspend fun removeReferences(identity: FoodDiaryEntryIdentity) {
        dao.deleteByEntryId(identity.id)
    }
}
