package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import kotlin.uuid.Uuid

@Dao
interface FoodDiaryCompositionDao {
    @Query(
        """
            SELECT entryId
            FROM FoodDiaryEntryReference
            WHERE componentIdentity = :identity
            """
    )
    suspend fun findEntriesByComponent(
        identity: FoodCompositionComponentIdentity.Identified
    ): List<Uuid>

    @Query(
        """
            DELETE FROM FoodDiaryEntryReference
            WHERE
                entryId = :entryId
            """
    )
    suspend fun deleteByEntryId(entryId: Uuid)

    @Insert suspend fun insertAll(references: List<FoodDiaryEntryReferenceEntity>)

    @Transaction
    suspend fun updateReferences(entryId: Uuid, references: List<FoodDiaryEntryReferenceEntity>) {
        deleteByEntryId(entryId)
        insertAll(references)
    }
}
