package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface ProductWithMeasurement {
    val product: Product
    val measurement: WeightMeasurement

    val weight: Float
        get() = measurement.getWeight(product)

    val calories: Float
        get() = product.nutrients.calories(weight)

    val proteins: Float
        get() = product.nutrients.proteins(weight)

    val carbohydrates: Float
        get() = product.nutrients.carbohydrates(weight)

    val fats: Float
        get() = product.nutrients.fats(weight)

    fun get(nutrient: Nutrient): Float? = product.nutrients.get(nutrient, weight)
}
