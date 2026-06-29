package com.maksimowiczm.foodyou.common.domain

import com.maksimowiczm.foodyou.common.Div
import com.maksimowiczm.foodyou.common.Plus
import com.maksimowiczm.foodyou.common.Times
import kotlinx.serialization.Serializable

/**
 * Represents a weight amount.
 *
 * Internally, weight is always stored in grams and converted at boundaries.
 */
@Serializable
class Weight(val grams: Double, val unit: WeightUnit) :
    Comparable<Weight>, Plus<Weight>, Times<Weight>, Div<Weight> {
    fun toDouble(unit: WeightUnit): Double = unit.fromGrams(grams)

    fun inUnit(unit: WeightUnit): Weight = ofGrams(grams, unit)

    override operator fun plus(other: Weight): Weight = ofGrams(grams + other.grams, unit)

    operator fun minus(other: Weight): Weight = ofGrams(grams - other.grams, unit)

    override operator fun times(scale: Number): Weight {
        val scale = scale.toDouble()
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale >= 0.0) { "Scale must be non-negative" }
        return ofGrams(grams * scale, unit)
    }

    override operator fun div(scale: Number): Weight {
        val scale = scale.toDouble()
        require(scale.isFinite()) { "Scale must be finite" }
        require(scale > 0.0) { "Scale must be greater than zero" }
        return ofGrams(grams / scale, unit)
    }

    operator fun div(other: Weight): Double {
        require(other.grams > 0.0) { "Cannot divide by zero weight" }
        return grams / other.grams
    }

    override operator fun compareTo(other: Weight): Int = grams.compareTo(other.grams)

    override fun toString(): String = "${toDouble(unit)} ${unit.symbol}"

    override fun equals(other: Any?): Boolean = other is Weight && grams == other.grams

    override fun hashCode(): Int = grams.hashCode()

    companion object {
        private val PARSE_REGEX = Regex("""^([\d.]+)\s*(.+)$""")

        fun parseOrNull(value: String): Weight? {
            val match = PARSE_REGEX.matchEntire(value.trim()) ?: return null
            val (amountStr, unitStr) = match.destructured
            val amount = amountStr.toDoubleOrNull() ?: return null
            val unit = WeightUnit.fromSymbolOrNull(unitStr) ?: return null

            return try {
                from(amount, unit)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun parse(value: String): Weight =
            parseOrNull(value) ?: throw IllegalArgumentException("Invalid weight format: $value")

        fun from(value: Double, unit: WeightUnit): Weight = ofGrams(unit.toGrams(value), unit)

        fun grams(value: Double): Weight = from(value, WeightUnit.Grams)

        fun milligrams(value: Double): Weight = from(value, WeightUnit.Milligrams)

        fun micrograms(value: Double): Weight = from(value, WeightUnit.Micrograms)

        fun ounces(value: Double): Weight = from(value, WeightUnit.Ounces)

        private fun ofGrams(value: Double, unit: WeightUnit): Weight {
            require(value.isFinite()) { "Weight must be finite" }
            require(value >= 0.0) { "Weight cannot be negative" }
            return Weight(value, unit)
        }
    }
}

@Serializable
enum class WeightUnit {
    Micrograms,
    Milligrams,
    Grams,
    Ounces;

    val symbol: String
        get() =
            when (this) {
                Micrograms -> "mcg"
                Milligrams -> "mg"
                Grams -> "g"
                Ounces -> "oz"
            }

    fun toGrams(value: Double): Double =
        when (this) {
            Micrograms -> value / MICROGRAMS_IN_GRAM
            Milligrams -> value / MILLIGRAMS_IN_GRAM
            Grams -> value
            Ounces -> value * GRAMS_IN_OUNCE
        }

    fun fromGrams(value: Double): Double =
        when (this) {
            Micrograms -> value * MICROGRAMS_IN_GRAM
            Milligrams -> value * MILLIGRAMS_IN_GRAM
            Grams -> value
            Ounces -> value / GRAMS_IN_OUNCE
        }

    companion object {
        fun fromSymbolOrNull(symbol: String): WeightUnit? =
            when (symbol.lowercase().trim().replace(".", "")) {
                "g",
                "grm",
                "grams" -> Grams
                "mg",
                "milligrams" -> Milligrams
                "mcg",
                "ug",
                "µg",
                "micrograms" -> Micrograms
                "oz",
                "ounces" -> Ounces
                else -> null
            }

        private const val MICROGRAMS_IN_GRAM = 1_000_000.0
        private const val MILLIGRAMS_IN_GRAM = 1_000.0
        private const val GRAMS_IN_OUNCE = 28.3495
    }
}

fun Double.toWeight(unit: WeightUnit): Weight = Weight.from(this, unit)

inline val Weight.milligrams: Double
    get() = toDouble(WeightUnit.Milligrams)

inline val Weight.micrograms: Double
    get() = toDouble(WeightUnit.Micrograms)

inline val Weight.ounces: Double
    get() = toDouble(WeightUnit.Ounces)

inline val Int.grams: Weight
    get() = Weight.grams(toDouble())

inline val Double.grams: Weight
    get() = Weight.grams(this)

inline val Int.milligrams: Weight
    get() = Weight.milligrams(toDouble())

inline val Double.milligrams: Weight
    get() = Weight.milligrams(this)

inline val Int.micrograms: Weight
    get() = Weight.micrograms(toDouble())

inline val Double.micrograms: Weight
    get() = Weight.micrograms(this)

inline val Int.ounces: Weight
    get() = Weight.ounces(toDouble())

inline val Double.ounces: Weight
    get() = Weight.ounces(this)

fun Iterable<Weight>.sum(unit: WeightUnit = WeightUnit.Grams) =
    fold(Weight.from(0.0, unit)) { acc, weight -> acc + weight.inUnit(unit) }
