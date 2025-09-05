package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementSuggestionRepository {
    suspend fun insert(foodId: FoodId, measurement: Measurement)

    fun observeByFoodId(foodId: FoodId, limit: Int): Flow<List<Measurement>>
}
