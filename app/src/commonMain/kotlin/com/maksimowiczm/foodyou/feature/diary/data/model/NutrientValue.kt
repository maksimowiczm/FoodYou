package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.ext.sumOf

sealed interface NutrientValue {
    val value: Float?

    @JvmInline
    value class Complete(override val value: Float) : NutrientValue

    @JvmInline
    value class Incomplete(override val value: Float?) : NutrientValue

    companion object {
        fun from(value: Float?) = if (value == null) {
            Incomplete(value)
        } else {
            Complete(value)
        }

        fun Float?.toNutrientValue() = if (this == null) {
            Incomplete(this)
        } else {
            Complete(this)
        }
    }

    operator fun plus(other: NutrientValue): NutrientValue = when (this) {
        is Complete -> when (other) {
            is Complete -> Complete(value + other.value)
            is Incomplete -> Incomplete(value + (other.value ?: 0f))
        }

        is Incomplete -> when (other) {
            is Complete -> Incomplete(other.value + (value ?: 0f))
            is Incomplete if (value == null && other.value == null) -> Incomplete(null)
            is Incomplete -> Incomplete((value ?: 0f) + (other.value ?: 0f))
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

fun List<NutrientValue>.sumOf(): NutrientValue {
    val isAllComplete = all { it is NutrientValue.Complete }

    val sum = sumOf {
        when (it) {
            is NutrientValue.Complete -> it.value
            is NutrientValue.Incomplete -> 0f
        }
    }

    return if (isAllComplete) {
        NutrientValue.Complete(sum)
    } else {
        NutrientValue.Incomplete(sum)
    }
}
