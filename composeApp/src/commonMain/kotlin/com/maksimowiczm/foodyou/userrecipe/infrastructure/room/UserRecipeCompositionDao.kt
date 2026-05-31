package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlin.uuid.Uuid

@Dao
interface UserRecipeCompositionDao {

    @Query(
        """
            SELECT recipeId
            FROM UserRecipeCompositionReference
            WHERE
                componentType = :type
                AND
                componentValue = :value
            """
    )
    suspend fun findRecipesByComponent(type: String, value: String): List<Uuid>

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
