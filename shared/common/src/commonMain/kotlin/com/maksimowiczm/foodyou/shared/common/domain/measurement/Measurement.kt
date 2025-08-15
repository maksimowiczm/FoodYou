package com.maksimowiczm.foodyou.shared.common.domain.measurement

import kotlin.jvm.JvmInline

sealed interface Measurement {

    operator fun times(other: Double): Measurement

    sealed interface ImmutableMeasurement : Measurement {

        /** The metric value of the measurement in grams or milliliters. */
        val metric: Double
    }

    data class Gram(val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Gram(value * other)
    }

    data class Milliliter(val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Milliliter(value * other)
    }

    data class Ounce(val value: Double) : ImmutableMeasurement {
        override val metric: Double = value / OUNCES_IN_GRAM

        override fun times(other: Double): ImmutableMeasurement = Ounce(value * other)

        companion object {
            const val OUNCES_IN_GRAM = 0.03527396
        }
    }

    @JvmInline
    value class Package(val quantity: Double) : Measurement {

        /** Calculates the weight of the package based on the given package weight. */
        fun weight(packageWeight: Double): Double = packageWeight * quantity

        override fun times(other: Double): Measurement = Package(quantity * other)
    }

    @JvmInline
    value class Serving(val quantity: Double) : Measurement {

        /** Calculates the weight of the serving based on the given serving weight. */
        fun weight(servingWeight: Double): Double = servingWeight * quantity

        override fun times(other: Double): Measurement = Serving(quantity * other)
    }

    companion object
}
