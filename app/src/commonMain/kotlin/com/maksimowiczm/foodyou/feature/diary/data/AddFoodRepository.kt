package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow

interface AddFoodRepository {

    fun observeProductByMeasurementId(
        measurementId: Long
    ): Flow<ProductWithMeasurement.Measurement?>
}
