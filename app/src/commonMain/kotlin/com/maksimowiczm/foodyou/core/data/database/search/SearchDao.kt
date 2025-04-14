package com.maksimowiczm.foodyou.core.data.database.search

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maksimowiczm.foodyou.core.data.model.search.SearchQueryEntity
import com.maksimowiczm.foodyou.core.data.source.SearchLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchDao : SearchLocalDataSource {
    @Query(
        """
        SELECT *
        FROM SearchQueryEntity
        ORDER BY epochSeconds DESC
        LIMIT :limit
    """
    )
    abstract override fun observeRecentQueries(limit: Int): Flow<List<SearchQueryEntity>>

    @Upsert
    abstract override suspend fun upsert(query: SearchQueryEntity)
}
