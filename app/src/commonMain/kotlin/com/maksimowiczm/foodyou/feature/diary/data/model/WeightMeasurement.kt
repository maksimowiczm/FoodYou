package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface WeightMeasurement {

    data class WeightUnit(val weight: Float) : WeightMeasurement
    data class Package(val quantity: Float) : WeightMeasurement
    data class Serving(val quantity: Float) : WeightMeasurement

    fun asEnum(): WeightMeasurementEnum = when (this) {
        is WeightUnit -> WeightMeasurementEnum.WeightUnit
        is Package -> WeightMeasurementEnum.Package
        is Serving -> WeightMeasurementEnum.Serving
    }

    /**
     * Helper function to get the total weight of the product based on the measurement.
     */
    fun getWeight(product: Product): Float = getWeight(product.packageWeight, product.servingWeight)

    fun getWeight(packageWeight: Float?, servingWeight: Float?): Float = when (this) {
        is WeightUnit -> weight
        is Package -> packageWeight!! * quantity
        is Serving -> servingWeight!! * quantity
    }
}

fun WeightMeasurementEnum.toWeightMeasurement(quantity: Float): WeightMeasurement = when (this) {
    WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
    WeightMeasurementEnum.Package -> WeightMeasurement.Package(quantity)
    WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(quantity)
}
