package com.maksimowiczm.foodyou.feature.measurement.domain

import kotlin.jvm.JvmInline

sealed interface Measurement {

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Milliliter(val value: Float) : Measurement

    @JvmInline
    value class Package(val quantity: Float) : Measurement {

        /**
         * Calculates the weight of the package based on the given package weight.
         */
        fun weight(packageWeight: Float) = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Float) : Measurement {

        /**
         * Calculates the weight of the serving based on the given serving weight.
         */
        fun weight(servingWeight: Float) = servingWeight * quantity
    }

    companion object {

        fun equal(a: Measurement, b: Measurement): Boolean = when {
            a is Gram && b is Gram -> a.value == b.value
            a is Milliliter && b is Milliliter -> a.value == b.value
            a is Package && b is Package -> a.quantity == b.quantity
            a is Serving && b is Serving -> a.quantity == b.quantity
            else -> false
        }

        fun notEqual(a: Measurement, b: Measurement): Boolean = !equal(a, b)
    }
}
