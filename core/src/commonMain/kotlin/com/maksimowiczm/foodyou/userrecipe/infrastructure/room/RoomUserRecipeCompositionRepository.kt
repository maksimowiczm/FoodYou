package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomUserRecipeCompositionRepository(private val dao: UserRecipeCompositionDao) :
    UserRecipeCompositionRepository {
    override suspend fun findRecipesUsing(
        identity: FoodSnapshotId.Tracked
    ): List<UserRecipeIdentity> {
        return dao.findRecipesBySnapshotId(identity).map(::UserRecipeIdentity)
    }

    override fun observeAncestors(identity: UserRecipeIdentity): Flow<Set<UserRecipeIdentity>> =
        dao.observeAncestors(identity.id.toString()).map { ids ->
            ids.map(::UserRecipeIdentity).toSet()
        }

    override suspend fun saveReferences(
        recipeIdentity: UserRecipeIdentity,
        identities: Set<FoodSnapshotId.Tracked>,
    ) {
        val references = identities.map { identity ->
            UserRecipeCompositionReferenceEntity(
                recipeId = recipeIdentity.id,
                snapshotId = identity,
            )
        }
        dao.updateReferences(recipeIdentity.id, references)
    }

    override suspend fun removeReferences(recipeIdentity: UserRecipeIdentity) {
        dao.deleteByRecipeId(recipeIdentity.id)
    }
}
