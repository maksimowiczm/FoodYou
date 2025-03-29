package com.maksimowiczm.foodyou.feature.diary.database.search

import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class SearchEntity(
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
    val packageWeight: Float?,

    // For products
    val servingWeight: Float?,

    // For recipes
    val servings: Int?,

    // Measurement data
    val measurement: WeightMeasurementEnum?,
    val quantity: Float?
)
