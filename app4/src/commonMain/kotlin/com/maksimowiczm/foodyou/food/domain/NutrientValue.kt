package com.maksimowiczm.foodyou.food.domain

import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * Sealed interface representing a nutrient value with completeness tracking.
 *
 * Nutrient values can be complete (fully known) or incomplete (missing or partial data). This type
 * ensures type-safe arithmetic operations while preserving data quality information throughout
 * calculations.
 *
 * ## Arithmetic Rules
 * - Complete + Complete = Complete
 * - Complete + Incomplete = Incomplete
 * - Incomplete + Incomplete = Incomplete (treats null as 0.0)
 * - Multiplication and division preserve completeness status
 */
sealed interface NutrientValue {
    /** The numeric value, or null if unknown. */
    val value: Double?

    /**
     * Represents a nutrient value that is known and complete.
     *
     * @property value The complete numeric value
     */
    @JvmInline
    value class Complete(override val value: Double) : NutrientValue {
        operator fun plus(other: Complete) = Complete(value + other.value)

        override operator fun times(other: Double) = Complete(value * other)

        override operator fun div(other: Double) = Complete(value / other)
    }

    /**
     * Represents a nutrient value that is not known or incomplete.
     *
     * This occurs when not all ingredients have complete nutritional data.
     *
     * @property value The partial numeric value, or null if completely unknown
     */
    @JvmInline
    value class Incomplete(override val value: Double?) : NutrientValue {
        operator fun plus(other: Incomplete) =
            when {
                value == null && other.value == null -> Incomplete(null)
                else -> Incomplete((value ?: 0.0) + (other.value ?: 0.0))
            }
    }

    operator fun plus(other: NutrientValue): NutrientValue =
        when (this) {
            is Complete ->
                when (other) {
                    is Complete -> this + other
                    is Incomplete -> Incomplete(value + (other.value ?: 0.0))
                }

            is Incomplete ->
                when (other) {
                    is Complete -> Incomplete(other.value + (value ?: 0.0))
                    is Incomplete -> this + other
                }
        }

    operator fun times(other: Double): NutrientValue =
        when (this) {
            is Complete -> Complete(value * other)
            is Incomplete -> Incomplete(value?.times(other))
        }

    operator fun div(other: Double): NutrientValue =
        when (this) {
            is Complete -> Complete(value / other)
            is Incomplete -> Incomplete(value?.div(other))
        }

    companion object {
        /** Creates a complete nutrient value from a non-null Double. */
        fun from(value: Double) = Complete(value)

        /** Creates a nutrient value from a nullable Double. */
        fun from(value: Double?) =
            when (value) {
                null -> Incomplete(value)
                else -> Complete(value)
            }

        /** Converts a nullable Double to a NutrientValue. */
        fun Double?.toNutrientValue() = from(this)

        /** Converts a nullable Float to a NutrientValue. */
        fun Float?.toNutrientValue() = from(this?.toDouble())
    }
}

/**
 * Type-safe check if the nutrient value is complete.
 *
 * Uses a contract to smart-cast the receiver to NutrientValue.Complete.
 */
fun NutrientValue.isComplete(): Boolean {
    contract { returns(true) implies (this@isComplete is NutrientValue.Complete) }

    return this is NutrientValue.Complete
}

/**
 * Type-safe check if the nutrient value is incomplete.
 *
 * Uses a contract to smart-cast the receiver to NutrientValue.Incomplete.
 */
fun NutrientValue.isIncomplete(): Boolean {
    contract { returns(true) implies (this@isIncomplete is NutrientValue.Incomplete) }

    return this is NutrientValue.Incomplete
}

/**
 * Sums a list of nutrient values, preserving completeness information.
 *
 * @return Complete if all values are complete, otherwise Incomplete
 */
fun List<NutrientValue>.sum(): NutrientValue =
    this.fold<NutrientValue, NutrientValue>(NutrientValue.Complete(0.0)) { acc, nutrientValue ->
        acc + nutrientValue
    }
