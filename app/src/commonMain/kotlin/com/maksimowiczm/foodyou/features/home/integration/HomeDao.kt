package com.maksimowiczm.foodyou.features.home.integration

import androidx.room3.*
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
abstract class HomeDao {
    @Insert protected abstract suspend fun insertAll(entities: List<HomeEntryEntity>)

    @Query("SELECT * FROM HomeEntry WHERE entryId = :entryId")
    protected abstract suspend fun findAllById(entryId: Uuid): List<HomeEntryEntity>

    @Update protected abstract suspend fun updateAll(entities: List<HomeEntryEntity>)

    @Query("DELETE FROM HomeEntry WHERE entryId = :entryId")
    abstract suspend fun delete(entryId: Uuid)

    @Query("UPDATE HomeEntry SET mealId = NULL WHERE entryId = :entryId")
    abstract suspend fun clearMealId(entryId: Uuid)

    @Transaction
    open suspend fun replaceEntries(entryId: Uuid, entities: List<HomeEntryEntity>) {
        delete(entryId)
        insertAll(entities)
    }

    @Transaction
    open suspend fun updateEach(
        entryId: Uuid,
        transform: (HomeEntryEntity) -> HomeEntryEntity,
    ) {
        updateAll(findAllById(entryId).map(transform))
    }

    @Query(
        """
            SELECT *
            FROM HomeEntry
            WHERE
                profileId = :profileId AND
                date = :date
            ORDER BY time ASC
            """
    )
    abstract fun observeEntries(profileId: Uuid, date: LocalDate): Flow<List<HomeEntryEntity>>
}
