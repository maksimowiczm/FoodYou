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

    companion object {
        fun defaultForProduct(product: Product) = when {
            product.servingWeight != null -> Serving(1f)
            product.packageWeight != null -> Package(1f)
            else -> WeightUnit(100f)
        }
    }

    fun getWeight(product: Product): Float = when (this) {
        is WeightUnit -> weight
        is Package -> product.packageWeight!! * quantity
        is Serving -> product.servingWeight!! * quantity
    }
}
