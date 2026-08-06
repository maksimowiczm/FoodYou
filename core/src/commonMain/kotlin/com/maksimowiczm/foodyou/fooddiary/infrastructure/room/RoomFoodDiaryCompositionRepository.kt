package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity

internal class RoomFoodDiaryCompositionRepository(private val dao: FoodDiaryCompositionDao) :
    FoodDiaryCompositionRepository {
    override suspend fun findEntriesUsing(
        identity: FoodCompositionComponentIdentity.Identified
    ): List<FoodDiaryEntryIdentity> {
        return dao.findEntriesByComponent(identity).map(::FoodDiaryEntryIdentity)
    }

    override suspend fun saveReferences(
        identity: FoodDiaryEntryIdentity,
        identities: Set<FoodCompositionComponentIdentity.Identified>,
    ) {
        val references = identities.map { componentIdentity ->
            FoodDiaryEntryReferenceEntity(
                entryId = identity.id,
                componentIdentity = componentIdentity,
            )
        }
        dao.updateReferences(identity.id, references)
    }

    override suspend fun removeReferences(identity: FoodDiaryEntryIdentity) {
        dao.deleteByEntryId(identity.id)
    }
}
