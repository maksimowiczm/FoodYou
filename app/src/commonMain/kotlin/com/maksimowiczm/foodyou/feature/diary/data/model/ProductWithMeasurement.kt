package com.maksimowiczm.foodyou.feature.diary.data.model

/**
 * Represents a product with a specific weight measurement
 */
sealed interface ProductWithMeasurement {
    val product: Product
    val measurement: WeightMeasurement

    val weight: Float
        get() = when (val measurement = measurement) {
            is WeightMeasurement.WeightUnit -> measurement.weight
            is WeightMeasurement.Package -> product.packageWeight!! * measurement.quantity
            is WeightMeasurement.Serving -> product.servingWeight!! * measurement.quantity
        }

    val calories: Float
        get() = product.nutrients.calories(weight)

    val proteins: Float
        get() = product.nutrients.proteins(weight)

    val carbohydrates: Float
        get() = product.nutrients.carbohydrates(weight)

    val fats: Float
        get() = product.nutrients.fats(weight)

    fun get(nutrient: Nutrient): Float? = product.nutrients.get(nutrient, weight)

    data class Measurement(
        override val product: Product,
        override val measurement: WeightMeasurement,
        val measurementId: Long
    ) : ProductWithMeasurement

    data class Suggestion(
        override val product: Product,
        override val measurement: WeightMeasurement
    ) : ProductWithMeasurement
}
