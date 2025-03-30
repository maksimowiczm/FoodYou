package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryMeasuredProduct
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
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

    suspend fun removeMeasurement(id: MeasurementId)

    suspend fun restoreMeasurement(id: MeasurementId)

    suspend fun updateMeasurement(id: MeasurementId, weightMeasurement: WeightMeasurement)

    fun observeMeasurements(mealId: Long?, date: LocalDate): Flow<List<DiaryMeasuredProduct>>

    fun observeMeasurementById(measurementId: MeasurementId): Flow<DiaryMeasuredProduct?>

    fun observeMeasurementSuggestionByProductId(productId: Long): Flow<List<WeightMeasurement>>
}
