package com.maksimowiczm.foodyou.food.domain.repository

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import kotlinx.coroutines.flow.Flow

interface FoodMeasurementSuggestionRepository {
    suspend fun insert(foodId: FoodId, measurement: Measurement)

    fun observeByFoodId(foodId: FoodId, limit: Int): Flow<List<Measurement>>
}
