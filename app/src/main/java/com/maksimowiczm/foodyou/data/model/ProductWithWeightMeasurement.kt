package com.maksimowiczm.foodyou.data.model

import kotlin.math.roundToInt

data class ProductWithWeightMeasurement(
    val product: Product,
    val measurementId: Long?,
    val measurement: WeightMeasurement
) {
    val weight: Float
        get() = measurement.weight

    val calories: Int
        get() = product.nutrients.calories(weight).roundToInt()

    val proteins: Int
        get() = product.nutrients.proteins(weight).roundToInt()

    val carbohydrates: Int
        get() = product.nutrients.carbohydrates(weight).roundToInt()

    val fats: Int
        get() = product.nutrients.fats(weight).roundToInt()
}
