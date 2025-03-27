package com.maksimowiczm.foodyou.feature.diary.domain.model

sealed interface WeightMeasurement {
    /**
     * Weight in grams
     */
    val weight: Float

    data class WeightUnit(override val weight: Float) : WeightMeasurement

    data class Package(val quantity: Float, val packageWeight: Float) : WeightMeasurement {
        override val weight: Float
            get() = quantity * packageWeight
    }

    data class Serving(val quantity: Float, val servingWeight: Float) : WeightMeasurement {
        override val weight: Float
            get() = quantity * servingWeight
    }
}