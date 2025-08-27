package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeSearchHistory(limit: Int = 10): Flow<List<SearchHistory>>

    suspend fun insertSearchHistory(entry: SearchHistory)
}
