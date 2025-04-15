package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.search.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

interface SearchLocalDataSource {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQueryEntity>>
    suspend fun upsert(query: SearchQueryEntity)
}
