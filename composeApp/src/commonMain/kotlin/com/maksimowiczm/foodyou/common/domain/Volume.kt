package com.maksimowiczm.foodyou.common.domain

import kotlinx.serialization.Serializable

/**
 * Represents a volume amount.
 *
 * Internally, volume is stored as milliliters.
 */
@Serializable
class Volume(val milliliters: Double, val unit: VolumeUnit) : Comparable<Volume> {
    fun toDouble(unit: VolumeUnit): Double = unit.fromMilliliters(milliliters)

    fun inUnit(unit: VolumeUnit): Volume = ofMilliliters(milliliters, unit)

    operator fun plus(other: Volume): Volume = ofMilliliters(milliliters + other.milliliters, unit)

    operator fun minus(other: Volume): Volume = ofMilliliters(milliliters - other.milliliters, unit)

    operator fun times(scale: Double): Volume {
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale >= 0.0) { "Scale must be non-negative" }
        return ofMilliliters(milliliters * scale, unit)
    }

    operator fun div(scale: Double): Volume {
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale > 0.0) { "Scale must be greater than zero" }
        return ofMilliliters(milliliters / scale, unit)
    }

    operator fun div(other: Volume): Double {
        require(other.milliliters > 0.0) { "Cannot divide by zero volume" }
        return milliliters / other.milliliters
    }

    override operator fun compareTo(other: Volume): Int = milliliters.compareTo(other.milliliters)

    override fun toString(): String = "${toDouble(unit)} ${unit.symbol}"

    override fun equals(other: Any?): Boolean = other is Volume && milliliters == other.milliliters

    override fun hashCode(): Int = milliliters.hashCode()

    companion object {
        fun from(value: Double, unit: VolumeUnit): Volume =
            ofMilliliters(unit.toMilliliters(value), unit)

        fun milliliters(value: Double): Volume = from(value, VolumeUnit.Milliliters)

        fun fluidOunces(value: Double): Volume = from(value, VolumeUnit.FluidOunces)

        private fun ofMilliliters(value: Double, unit: VolumeUnit): Volume {
            require(value.isFinite()) { "Volume must be finite" }
            require(value >= 0.0) { "Volume cannot be negative" }
            return Volume(value, unit)
        }
    }
}

enum class VolumeUnit {
    Milliliters,
    FluidOunces;

    val symbol: String
        get() =
            when (this) {
                Milliliters -> "ml"
                FluidOunces -> "floz"
            }

    fun toMilliliters(value: Double): Double =
        when (this) {
            Milliliters -> value
            FluidOunces -> value * MILLILITERS_IN_FLUID_OUNCE
        }

    fun fromMilliliters(value: Double): Double =
        when (this) {
            Milliliters -> value
            FluidOunces -> value / MILLILITERS_IN_FLUID_OUNCE
        }

    private companion object {
        const val MILLILITERS_IN_FLUID_OUNCE = 29.5735
    }
}

fun Double.toVolume(unit: VolumeUnit): Volume = Volume.from(this, unit)

inline val Volume.fluidOunces: Double
    get() = toDouble(VolumeUnit.FluidOunces)

inline val Int.milliliters: Volume
    get() = Volume.milliliters(toDouble())

inline val Double.milliliters: Volume
    get() = Volume.milliliters(this)

inline val Int.fluidOunces: Volume
    get() = Volume.fluidOunces(toDouble())

inline val Double.fluidOunces: Volume
    get() = Volume.fluidOunces(this)
