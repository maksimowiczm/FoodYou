package com.maksimowiczm.foodyou.feature.diary.database.measurement

import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class CombinedMeasurement(
    // Identifiers
    val productId: Long?,
    val recipeId: Long?,

    // Diary
    val mealId: Long,
    val diaryEpochDay: Int,

    // Measurement
    val measurementId: Long,
    val measurement: WeightMeasurementEnum,
    val quantity: Float,

    // Rest
    val createdAt: Long,
    val isDeleted: Boolean
)
