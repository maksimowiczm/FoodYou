package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomUserRecipeCompositionRepository(private val dao: UserRecipeCompositionDao) :
    UserRecipeCompositionRepository {
    override suspend fun findRecipesUsing(id: FoodSnapshotId.Tracked): List<UserRecipeId> {
        return dao.findRecipesBySnapshotId(id).map(::UserRecipeId)
    }

    override fun observeAncestors(id: UserRecipeId): Flow<Set<UserRecipeId>> =
        dao.observeAncestors(id.value.toString()).map { ids ->
            ids.map(::UserRecipeId).toSet()
        }

    override suspend fun saveReferences(
        id: UserRecipeId,
        trackedIds: Set<FoodSnapshotId.Tracked>,
    ) {
        val references = trackedIds.map { snapshotId ->
            UserRecipeCompositionReferenceEntity(
                recipeId = id.value,
                snapshotId = snapshotId,
            )
        }
        dao.updateReferences(id.value, references)
    }

    override suspend fun removeReferences(id: UserRecipeId) {
        dao.deleteByRecipeId(id.value)
    }
}
