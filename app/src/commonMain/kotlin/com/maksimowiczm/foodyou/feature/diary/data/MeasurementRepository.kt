package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MeasurementRepository {
    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        productId: Long,
        weightMeasurement: WeightMeasurement
    )

    suspend fun removeMeasurement(measurementId: Long)

    suspend fun restoreMeasurement(measurementId: Long)

    suspend fun updateMeasurement(measurementId: Long, weightMeasurement: WeightMeasurement)

    fun observeMeasurements(
        mealId: Long?,
        date: LocalDate
    ): Flow<List<ProductWithMeasurement.Measurement>>
}
