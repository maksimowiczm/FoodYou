package com.maksimowiczm.foodyou.core.database.search

import com.maksimowiczm.foodyou.core.database.measurement.Measurement

data class FoodSearchVirtualEntity(
    // Identity
    val productId: Long,
    val epochDay: Int,
    val mealId: Long,

    // Food
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurementId: Long?,
    val measurement: Measurement,
    val quantity: Float
)
