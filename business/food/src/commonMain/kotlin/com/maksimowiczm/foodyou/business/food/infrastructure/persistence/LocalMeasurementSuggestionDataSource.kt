package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.coroutines.flow.Flow

internal interface LocalMeasurementSuggestionDataSource {

    suspend fun insert(foodId: FoodId, measurement: Measurement)

    fun observeByFoodId(foodId: FoodId, limit: Int): Flow<List<Measurement>>
}
