package com.maksimowiczm.foodyou.core.domain.model

sealed interface NutrientValue {
    val value: Float?

    /**
     * Represents a nutrient value that is known and complete.
     */
    @JvmInline
    value class Complete(override val value: Float) : NutrientValue {
        operator fun plus(other: Complete) = Complete(value + other.value)
        override operator fun times(other: Float) = Complete(value * other)
        override operator fun div(other: Float) = Complete(value / other)
    }

    /**
     * Represents a nutrient value that is not known or incomplete (e.g. not all ingredients have
     * complete data).
     */
    @JvmInline
    value class Incomplete(override val value: Float?) : NutrientValue {
        operator fun plus(other: Incomplete) = when {
            value == null && other.value == null -> Incomplete(null)
            else -> Incomplete((value ?: 0f) + (other.value ?: 0f))
        }
    }

    companion object {
        fun from(value: Float) = Complete(value)

        fun from(value: Float?) = if (value == null) {
            Incomplete(value)
        } else {
            Complete(value)
        }

        fun Float?.toNutrientValue() = from(this)

        fun Float.toNutrientValue() = from(this)
    }

    operator fun plus(other: NutrientValue): NutrientValue = when (this) {
        is Complete -> when (other) {
            is Complete -> this + other
            is Incomplete -> Incomplete(value + (other.value ?: 0f))
        }

        is Incomplete -> when (other) {
            is Complete -> Incomplete(other.value + (value ?: 0f))
            is Incomplete -> this + other
        }
    }

    operator fun times(other: Float): NutrientValue = when (this) {
        is Complete -> Complete(value * other)
        is Incomplete -> Incomplete(value?.times(other))
    }

    operator fun div(other: Float): NutrientValue = when (this) {
        is Complete -> Complete(value / other)
        is Incomplete -> Incomplete(value?.div(other))
    }
}

fun Iterable<NutrientValue.Complete>.sum() =
    fold(NutrientValue.Complete(0f)) { acc, nutrientValue ->
        acc + nutrientValue
    }
