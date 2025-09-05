package com.maksimowiczm.foodyou.business.shared.domain.measurement

import kotlin.jvm.JvmInline

sealed interface Measurement {

    operator fun times(other: Double): Measurement

    sealed interface ImmutableMeasurement : Measurement {

        /** The value of the measurement in its native unit. */
        val value: Double

        /** The metric value of the measurement in grams or milliliters. */
        val metric: Double
    }

    data class Gram(override val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Gram(value * other)

        companion object {
            const val DEFAULT = 100.0
        }
    }

    data class Milliliter(override val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Milliliter(value * other)

        companion object {
            const val DEFAULT = 100.0
        }
    }

    data class Ounce(override val value: Double) : ImmutableMeasurement {
        override val metric: Double = value / OUNCES_IN_GRAM

        override fun times(other: Double): ImmutableMeasurement = Ounce(value * other)

        companion object {
            const val OUNCES_IN_GRAM = 0.03527396
            const val DEFAULT = 1.0
        }
    }

    data class FluidOunce(override val value: Double) : ImmutableMeasurement {
        override val metric: Double = value / FLUID_OUNCES_IN_MILLILITER

        override fun times(other: Double): ImmutableMeasurement = FluidOunce(value * other)

        companion object {
            const val FLUID_OUNCES_IN_MILLILITER = 0.0338140227
            const val DEFAULT = 8.0
        }
    }

    @JvmInline
    value class Package(val quantity: Double) : Measurement {

        /** Calculates the weight of the package based on the given package weight. */
        fun weight(packageWeight: Double): Double = packageWeight * quantity

        override fun times(other: Double): Measurement = Package(quantity * other)

        companion object {
            const val DEFAULT = 1.0
        }
    }

    @JvmInline
    value class Serving(val quantity: Double) : Measurement {

        /** Calculates the weight of the serving based on the given serving weight. */
        fun weight(servingWeight: Double): Double = servingWeight * quantity

        override fun times(other: Double): Measurement = Serving(quantity * other)

        companion object {
            const val DEFAULT = 1.0
        }
    }

    companion object
}
