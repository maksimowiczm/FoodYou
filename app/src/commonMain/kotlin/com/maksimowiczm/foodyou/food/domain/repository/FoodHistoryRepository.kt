package com.maksimowiczm.foodyou.food.domain.repository

import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import kotlinx.coroutines.flow.Flow

interface FoodHistoryRepository {
    suspend fun insert(foodId: FoodId, history: FoodHistory)

    fun observeFoodHistory(foodId: FoodId): Flow<List<FoodHistory>>
}
