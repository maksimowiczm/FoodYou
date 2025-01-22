package com.maksimowiczm.foodyou.feature.addfood.data.model

sealed interface WeightMeasurement {
    val weight: Float

    data class WeightUnit(
        override val weight: Float
    ) : WeightMeasurement

    data class Package(
        val quantity: Float,
        val packageWeight: Float
    ) : WeightMeasurement {
        override val weight: Float
            get() = quantity * packageWeight
    }

    data class Serving(
        val quantity: Float,
        val servingWeight: Float
    ) : WeightMeasurement {
        override val weight: Float
            get() = quantity * servingWeight
    }

    fun asEnum(): WeightMeasurementEnum {
        return when (this) {
            is WeightUnit -> WeightMeasurementEnum.WeightUnit
            is Package -> WeightMeasurementEnum.Package
            is Serving -> WeightMeasurementEnum.Serving
        }
    }
}
