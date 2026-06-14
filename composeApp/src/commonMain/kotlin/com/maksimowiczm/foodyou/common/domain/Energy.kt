package com.maksimowiczm.foodyou.common.domain

import com.maksimowiczm.foodyou.common.Div
import com.maksimowiczm.foodyou.common.Plus
import com.maksimowiczm.foodyou.common.Times
import kotlinx.serialization.Serializable

/**
 * Represents an energy amount.
 *
 * Internally, energy is always stored in kilocalories and converted at boundaries.
 */
@Serializable
class Energy(val kilocalories: Double, val unit: EnergyUnit) :
    Comparable<Energy>, Plus<Energy>, Times<Energy>, Div<Energy> {
    fun toDouble(unit: EnergyUnit): Double = unit.fromKilocalories(kilocalories)

    fun inUnit(unit: EnergyUnit): Energy = ofKilocalories(kilocalories, unit)

    override operator fun plus(other: Energy): Energy =
        ofKilocalories(kilocalories + other.kilocalories, unit)

    operator fun minus(other: Energy): Energy =
        ofKilocalories(kilocalories - other.kilocalories, unit)

    override operator fun times(scale: Number): Energy {
        val scale = scale.toDouble()
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale >= 0.0) { "Scale must be non-negative" }
        return ofKilocalories(kilocalories * scale, unit)
    }

    override operator fun div(scale: Number): Energy {
        val scale = scale.toDouble()
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale > 0.0) { "Scale must be greater than zero" }
        return ofKilocalories(kilocalories / scale, unit)
    }

    operator fun div(other: Energy): Double {
        require(other.kilocalories > 0.0) { "Cannot divide by zero energy" }
        return kilocalories / other.kilocalories
    }

    override operator fun compareTo(other: Energy): Int = kilocalories.compareTo(other.kilocalories)

    override fun toString(): String = "${toDouble(unit)} ${unit.symbol}"

    override fun equals(other: Any?): Boolean =
        other is Energy && kilocalories == other.kilocalories

    override fun hashCode(): Int = kilocalories.hashCode()

    companion object {
        fun from(value: Double, unit: EnergyUnit): Energy =
            ofKilocalories(unit.toKilocalories(value), unit)

        fun kilocalories(value: Double): Energy = from(value, EnergyUnit.Kilocalories)

        fun kilojoules(value: Double): Energy = from(value, EnergyUnit.Kilojoules)

        private fun ofKilocalories(value: Double, unit: EnergyUnit): Energy {
            require(value.isFinite()) { "Energy must be finite" }
            require(value >= 0.0) { "Energy cannot be negative" }
            return Energy(value, unit)
        }
    }
}

@Serializable
enum class EnergyUnit {
    Kilocalories,
    Kilojoules;

    val symbol: String
        get() =
            when (this) {
                Kilocalories -> "kcal"
                Kilojoules -> "kJ"
            }

    fun toKilocalories(value: Double): Double =
        when (this) {
            Kilocalories -> value
            Kilojoules -> value / KILOJOULE_CONVERSION_FACTOR
        }

    fun fromKilocalories(value: Double): Double =
        when (this) {
            Kilocalories -> value
            Kilojoules -> value * KILOJOULE_CONVERSION_FACTOR
        }

    companion object {
        private const val KILOJOULE_CONVERSION_FACTOR = 4.184
    }
}

fun Double.toEnergy(unit: EnergyUnit): Energy = Energy.from(this, unit)

inline val Energy.kilojoules: Double
    get() = toDouble(EnergyUnit.Kilojoules)

inline val Int.kilocalories: Energy
    get() = Energy.kilocalories(toDouble())

inline val Double.kilocalories: Energy
    get() = Energy.kilocalories(this)

inline val Int.kilojoules: Energy
    get() = Energy.kilojoules(toDouble())

inline val Double.kilojoules: Energy
    get() = Energy.kilojoules(this)
