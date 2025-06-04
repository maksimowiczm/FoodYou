package com.maksimowiczm.foodyou.core.database.search

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchLocalDataSource {
    @Query(
        """
        SELECT *
        FROM SearchQueryEntity
        ORDER BY epochSeconds DESC
        LIMIT :limit
    """
    )
    fun observeRecentQueries(limit: Int): Flow<List<SearchQueryEntity>>

    @Upsert
    suspend fun upsert(query: SearchQueryEntity)
}
