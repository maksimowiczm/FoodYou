package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomUserRecipeCompositionRepository(database: UserRecipeDatabase) :
    UserRecipeCompositionRepository {
    private val dao: UserRecipeCompositionDao = database.compositionDao

    override suspend fun findRecipesUsing(
        identity: FoodCompositionComponentIdentity
    ): List<UserRecipeIdentity> {
        val (type, value) = identity.toTypeAndValue()
        return dao.findRecipesByComponent(type, value).map(::UserRecipeIdentity)
    }

    override fun observeAncestors(identity: UserRecipeIdentity): Flow<Set<UserRecipeIdentity>> =
        dao.observeAncestors(identity.id.toString()).map { ids ->
            ids.map(::UserRecipeIdentity).toSet()
        }

    override suspend fun saveReferences(
        recipeIdentity: UserRecipeIdentity,
        identities: Set<FoodCompositionComponentIdentity>,
    ) {
        val references = identities.map { identity ->
            val (type, value) = identity.toTypeAndValue()
            UserRecipeCompositionReferenceEntity(
                recipeId = recipeIdentity.id,
                componentType = type,
                componentValue = value,
            )
        }
        dao.updateReferences(recipeIdentity.id, references)
    }

    override suspend fun removeReferences(recipeIdentity: UserRecipeIdentity) {
        dao.deleteByRecipeId(recipeIdentity.id)
    }

    private fun FoodCompositionComponentIdentity.toTypeAndValue(): Pair<String, String> =
        when (this) {
            is FoodCompositionComponentIdentity.UserProduct -> "USER_PRODUCT" to id.toString()
            is FoodCompositionComponentIdentity.OpenFoodFacts -> "OPEN_FOOD_FACTS" to barcode
            is FoodCompositionComponentIdentity.FoodDataCentral ->
                "FOOD_DATA_CENTRAL" to fdcId.toString()

            is FoodCompositionComponentIdentity.Recipe -> "RECIPE" to id.toString()
        }
}
