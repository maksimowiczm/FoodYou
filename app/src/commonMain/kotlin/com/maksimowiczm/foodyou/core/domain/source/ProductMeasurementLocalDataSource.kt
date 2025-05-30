package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow

interface ProductMeasurementLocalDataSource {
    suspend fun addProductMeasurement(entity: ProductMeasurementEntity)
    suspend fun updateProductMeasurement(entity: ProductMeasurementEntity)
    suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?
    suspend fun deleteProductMeasurement(id: Long)
    fun observeProductMeasurements(epochDay: Int, mealId: Long): Flow<List<ProductWithMeasurement>>
    fun observeProductMeasurement(measurementId: Long): Flow<ProductWithMeasurement?>
    fun observeProductMeasurementSuggestions(productId: Long): Flow<List<MeasurementSuggestion>>
    fun observeLatestProductMeasurementSuggestion(productId: Long): Flow<MeasurementSuggestion?>

    fun observeMeasurementsByProductMealDay(
        productId: Long,
        mealId: Long,
        epochDay: Int
    ): Flow<List<ProductWithMeasurement>>
}
