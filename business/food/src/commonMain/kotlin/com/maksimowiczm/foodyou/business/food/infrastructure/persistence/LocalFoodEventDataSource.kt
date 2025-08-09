package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

internal interface LocalFoodEventDataSource {

    suspend fun insert(foodId: FoodId, event: FoodEvent)

    suspend fun observeFoodEvents(foodId: FoodId): Flow<List<FoodEvent>>
}
