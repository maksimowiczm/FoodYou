package com.maksimowiczm.foodyou.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.data.model.ProductQuery
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface AddFoodRepository {
    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        productId: Long,
        weightMeasurement: WeightMeasurement
    )

    suspend fun removeMeasurement(measurementId: Long)

    suspend fun restoreMeasurement(measurementId: Long)

    suspend fun updateMeasurement(measurementId: Long, weightMeasurement: WeightMeasurement)

    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<ProductWithWeightMeasurement>>

    fun observeQuantitySuggestionByProductId(productId: Long): Flow<QuantitySuggestion>

    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun observeProductByMeasurementId(measurementId: Long): Flow<ProductWithWeightMeasurement?>
}
