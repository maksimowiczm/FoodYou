package com.maksimowiczm.foodyou.search.infrastructure.history

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query(
        """
        SELECT * FROM SearchHistory 
        WHERE profileId = :profileId
        ORDER BY timestampMillis DESC
        LIMIT :limit
        """
    )
    fun observeHistory(profileId: Uuid, limit: Int): Flow<List<SearchHistoryEntity>>

    @Upsert suspend fun upsert(entities: List<SearchHistoryEntity>)
}
