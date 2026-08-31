package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSnapshotIdConverter.Companion.RECIPE
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSnapshotIdConverter.Companion.SEPARATOR
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeCompositionDao {

    @Query(
        """
            SELECT recipeId
            FROM UserRecipeFlattenedComposition
            WHERE
                snapshotId = :id
            """
    )
    suspend fun findRecipesBySnapshotId(id: FoodSnapshotId.Tracked): List<Uuid>

    @Query(
        """
            SELECT recipeId
            FROM UserRecipeFlattenedComposition
            WHERE
                snapshotId = '$RECIPE$SEPARATOR' || :recipeId
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
