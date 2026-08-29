package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodCompositionComponentIdentityConverter.Companion.RECIPE
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodCompositionComponentIdentityConverter.Companion.SEPARATOR
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeCompositionDao {

    @Query(
        """
            SELECT recipeId
            FROM UserRecipeFlattenedComposition
            WHERE
                componentIdentity = :identity
            """
    )
    suspend fun findRecipesByComponent(
        identity: FoodCompositionComponentIdentity.Identified
    ): List<Uuid>

    @Query(
        """
            SELECT recipeId
            FROM UserRecipeFlattenedComposition
            WHERE
                componentIdentity = '$RECIPE$SEPARATOR' || :recipeId
            """
    )
    fun observeAncestors(recipeId: String): Flow<List<Uuid>>

    @Query(
        """
            DELETE FROM UserRecipeCompositionReference
            WHERE
                recipeId = :recipeId
            """
    )
    suspend fun deleteByRecipeId(recipeId: Uuid)

    @Insert suspend fun insertAll(references: List<UserRecipeCompositionReferenceEntity>)

    @Transaction
    suspend fun updateReferences(
        recipeId: Uuid,
        references: List<UserRecipeCompositionReferenceEntity>,
    ) {
        deleteByRecipeId(recipeId)
        insertAll(references)
    }
}
