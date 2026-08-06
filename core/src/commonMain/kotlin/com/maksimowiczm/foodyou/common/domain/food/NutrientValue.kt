package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.Div
import com.maksimowiczm.foodyou.common.Plus
import com.maksimowiczm.foodyou.common.Times
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

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
@Serializable
sealed interface NutrientValue<T> where T : Plus<T>, T : Times<T>, T : Div<T> {
    /** The numeric value, or null if unknown. */
    val value: T?

    /**
     * Represents a nutrient value that is known and complete.
     *
     * @property value The complete numeric value
     */
    @Serializable
    @JvmInline
    value class Complete<T>(override val value: T) : NutrientValue<T>
        where T : Plus<T>, T : Times<T>, T : Div<T> {
        operator fun plus(other: Complete<T>): Complete<T> = Complete(value + other.value)

        operator fun plus(other: Incomplete<T>): Incomplete<T> =
            when {
                other.value != null -> Incomplete(value + other.value)
                else -> Incomplete(value)
            }
    }

    /**
     * Represents a nutrient value that is not known or incomplete.
     *
     * This occurs when not all ingredients have complete nutritional data.
     *
     * @property value The partial numeric value, or null if completely unknown
     */
    @Serializable
    @JvmInline
    value class Incomplete<T>(override val value: T?) : NutrientValue<T>
        where T : Plus<T>, T : Times<T>, T : Div<T> {
        operator fun plus(other: Complete<T>): Incomplete<T> = other + this

        operator fun plus(other: Incomplete<T>): Incomplete<T> =
            when {
                value != null && other.value != null -> Incomplete(value + other.value)
                value != null && other.value == null -> Incomplete(value)
                value == null && other.value != null -> Incomplete(other.value)
                else -> Incomplete(null)
            }
    }

    operator fun plus(other: NutrientValue<T>): NutrientValue<T> =
        when {
            this is Complete && other is Complete -> this + other
            this is Complete && other is Incomplete -> this + other
            this is Incomplete && other is Complete -> this + other
            this is Incomplete && other is Incomplete -> this + other
            else -> error("unreachable")
        }

    operator fun times(other: Number): NutrientValue<T> =
        when (this) {
            is Complete -> Complete(value * other)
            is Incomplete -> Incomplete(value?.times(other))
        }

    operator fun div(other: Number): NutrientValue<T> =
        when (this) {
            is Complete -> Complete(value / other)
            is Incomplete -> Incomplete(value?.div(other))
        }

    companion object {
        fun <T> T?.toNutrientValue(): NutrientValue<T> where T : Plus<T>, T : Times<T>, T : Div<T> =
            if (this != null) Complete(this) else Incomplete(null)

        fun <T> from(value: T?): NutrientValue<T> where T : Plus<T>, T : Times<T>, T : Div<T> =
            value.toNutrientValue()
    }
}

/**
 * Type-safe check if the nutrient value is complete.
 *
 * Uses a contract to smart-cast the receiver to NutrientValue.Complete.
 */
fun <T> NutrientValue<T>.isComplete(): Boolean where T : Plus<T>, T : Times<T>, T : Div<T> {
    contract { returns(true) implies (this@isComplete is NutrientValue.Complete) }

    return this is NutrientValue.Complete
}

/**
 * Type-safe check if the nutrient value is incomplete.
 *
 * Uses a contract to smart-cast the receiver to NutrientValue.Incomplete.
 */
fun <T> NutrientValue<T>.isIncomplete(): Boolean where T : Plus<T>, T : Times<T>, T : Div<T> {
    contract { returns(true) implies (this@isIncomplete is NutrientValue.Incomplete<T>) }

    return this is NutrientValue.Incomplete<T>
}

inline fun <T> NutrientValue<T>.map(transform: (T) -> T): NutrientValue<T>
    where T : Plus<T>, T : Times<T>, T : Div<T> {
    contract {
        callsInPlace(transform, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is NutrientValue.Complete -> NutrientValue.Complete(transform(value))
        is NutrientValue.Incomplete -> NutrientValue.Incomplete(value?.let(transform))
    }
}

/**
 * Sums a list of nutrient values, preserving completeness information.
 *
 * @return Complete if all values are complete, otherwise Incomplete
 */
fun <T> List<NutrientValue<T>>.sum(zero: T): NutrientValue<T>
    where T : Plus<T>, T : Times<T>, T : Div<T> =
    this.fold<NutrientValue<T>, NutrientValue<T>>(NutrientValue.Complete(zero)) { acc, nutrientValue
        ->
        acc + nutrientValue
    }
