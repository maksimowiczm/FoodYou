package com.maksimowiczm.foodyou.foodyou3.domain

import kotlin.jvm.JvmInline

sealed interface FoodYou3Measurement {

    operator fun times(other: Double): FoodYou3Measurement

    sealed interface ImmutableMeasurement : FoodYou3Measurement {

        /** The value of the measurement in its native unit. */
        val value: Double

        /** The metric value of the measurement in grams or milliliters. */
        val metric: Double
    }

    data class Gram(override val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Gram(value * other)
    }

    data class Milliliter(override val value: Double) : ImmutableMeasurement {
        override val metric: Double
            get() = value

        override fun times(other: Double): ImmutableMeasurement = Milliliter(value * other)
    }

    data class Ounce(override val value: Double) : ImmutableMeasurement {
        override val metric: Double = value / OUNCES_IN_GRAM

        override fun times(other: Double): ImmutableMeasurement = Ounce(value * other)

        companion object {
            const val OUNCES_IN_GRAM = 0.03527396
        }
    }

    data class FluidOunce(override val value: Double) : ImmutableMeasurement {
        override val metric: Double = value / FLUID_OUNCES_IN_MILLILITER

        override fun times(other: Double): ImmutableMeasurement = FluidOunce(value * other)

        companion object {
            const val FLUID_OUNCES_IN_MILLILITER = 0.0338140227
        }
    }

    @JvmInline
    value class Package(val quantity: Double) : FoodYou3Measurement {

        /** Calculates the weight of the package based on the given package weight. */
        fun weight(packageWeight: Double): Double = packageWeight * quantity

        override fun times(other: Double): FoodYou3Measurement = Package(quantity * other)
    }

    @JvmInline
    value class Serving(val quantity: Double) : FoodYou3Measurement {

        /** Calculates the weight of the serving based on the given serving weight. */
        fun weight(servingWeight: Double): Double = servingWeight * quantity

        override fun times(other: Double): FoodYou3Measurement = Serving(quantity * other)
    }

    companion object
}

enum class FoodYou3MeasurementType {
    Gram,
    Package,
    Serving,
    Milliliter,
    Ounce,
    FluidOunce,
}

val FoodYou3Measurement.type: FoodYou3MeasurementType
    get() =
        when (this) {
            is FoodYou3Measurement.Gram -> FoodYou3MeasurementType.Gram
            is FoodYou3Measurement.Milliliter -> FoodYou3MeasurementType.Milliliter
            is FoodYou3Measurement.Ounce -> FoodYou3MeasurementType.Ounce
            is FoodYou3Measurement.FluidOunce -> FoodYou3MeasurementType.FluidOunce
            is FoodYou3Measurement.Package -> FoodYou3MeasurementType.Package
            is FoodYou3Measurement.Serving -> FoodYou3MeasurementType.Serving
        }

fun FoodYou3Measurement.Companion.from(
    type: FoodYou3MeasurementType,
    rawValue: Double,
): FoodYou3Measurement =
    when (type) {
        FoodYou3MeasurementType.Gram -> FoodYou3Measurement.Gram(rawValue)
        FoodYou3MeasurementType.Milliliter -> FoodYou3Measurement.Milliliter(rawValue)
        FoodYou3MeasurementType.Package -> FoodYou3Measurement.Package(rawValue)
        FoodYou3MeasurementType.Serving -> FoodYou3Measurement.Serving(rawValue)
        FoodYou3MeasurementType.Ounce -> FoodYou3Measurement.Ounce(rawValue)
        FoodYou3MeasurementType.FluidOunce -> FoodYou3Measurement.FluidOunce(rawValue)
    }
