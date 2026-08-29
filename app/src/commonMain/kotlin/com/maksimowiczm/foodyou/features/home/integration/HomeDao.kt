package com.maksimowiczm.foodyou.features.home.integration

import androidx.room3.*
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
abstract class HomeDao {
    @Insert abstract suspend fun insert(entity: HomeEntryEntity)

    @Query("SELECT * FROM HomeEntry WHERE entryId = :entryId")
    protected abstract suspend fun findById(entryId: Uuid): HomeEntryEntity?

    @Update protected abstract suspend fun update(entity: HomeEntryEntity)

    @Transaction
    open suspend fun updateInTransaction(
        entryId: Uuid,
        transform: suspend (HomeEntryEntity) -> HomeEntryEntity,
    ) {
        // TODO
        //  might not want to silently drop the error here. How should we treat read model sync
        //  errors?
        val entity = findById(entryId) ?: return
        val updated = transform(entity)
        update(updated.copy(entryId = entryId))
    }

    @Query("DELETE FROM HomeEntry WHERE entryId = :entryId")
    abstract suspend fun delete(entryId: Uuid)

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
