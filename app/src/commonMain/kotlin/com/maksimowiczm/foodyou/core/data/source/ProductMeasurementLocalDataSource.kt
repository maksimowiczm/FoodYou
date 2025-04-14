package com.maksimowiczm.foodyou.core.data.source

import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow

interface ProductMeasurementLocalDataSource {
    suspend fun addProductMeasurement(entity: ProductMeasurementEntity)
    suspend fun updateProductMeasurement(entity: ProductMeasurementEntity)
    suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?
    suspend fun deleteProductMeasurement(id: Long)
    suspend fun restoreProductMeasurement(id: Long)
    fun observeProductMeasurements(epochDay: Int, mealId: Long): Flow<List<ProductWithMeasurement>>
    fun observeProductMeasurement(measurementId: Long): Flow<ProductWithMeasurement?>
    suspend fun getProductMeasurementSuggestion(productId: Long): MeasurementSuggestion
    suspend fun getProductMeasurementSuggestions(productId: Long): List<MeasurementSuggestion>
}
