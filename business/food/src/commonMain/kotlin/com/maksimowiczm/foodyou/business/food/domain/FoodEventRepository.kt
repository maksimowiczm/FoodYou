package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

interface FoodEventRepository {
    suspend fun insert(foodId: FoodId, event: FoodEvent)

    fun observeFoodEvents(foodId: FoodId): Flow<List<FoodEvent>>
}
