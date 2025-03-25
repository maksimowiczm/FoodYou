package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.QuantitySuggestion
import kotlinx.coroutines.flow.Flow

interface AddFoodRepository {

    fun observeQuantitySuggestionByProductId(productId: Long): Flow<QuantitySuggestion>

    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun observeProductByMeasurementId(
        measurementId: Long
    ): Flow<ProductWithMeasurement.Measurement?>
}
