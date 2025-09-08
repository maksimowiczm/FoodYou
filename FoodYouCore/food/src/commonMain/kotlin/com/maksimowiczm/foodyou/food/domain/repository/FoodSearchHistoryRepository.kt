package com.maksimowiczm.foodyou.food.domain.repository

import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchHistory
import kotlinx.coroutines.flow.Flow

interface FoodSearchHistoryRepository {
    fun observeHistory(limit: Int): Flow<List<FoodSearchHistory>>

    suspend fun insert(entry: FoodSearchHistory)
}
