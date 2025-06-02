package com.maksimowiczm.foodyou.core.domain.repository

import com.maksimowiczm.foodyou.core.domain.model.SearchQuery
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>
}
