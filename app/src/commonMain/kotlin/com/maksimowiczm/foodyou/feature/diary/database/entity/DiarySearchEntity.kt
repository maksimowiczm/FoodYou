package com.maksimowiczm.foodyou.feature.diary.database.entity

import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class DiarySearchEntity(
    // Identifiers
    val productId: Long?,
    val recipeId: Long?,

    // Search data
    val name: String,
    val brand: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,

    // Measurement data
    val measurementId: Long?,
    val measurement: WeightMeasurementEnum,
    val quantity: Float,
)
