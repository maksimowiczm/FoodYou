package com.maksimowiczm.foodyou.business.food.domain

import kotlin.jvm.JvmInline

sealed interface Measurement {

    @JvmInline value class Gram(val value: Double) : Measurement

    @JvmInline value class Milliliter(val value: Double) : Measurement

    @JvmInline
    value class Package(val quantity: Double) : Measurement {

        /** Calculates the weight of the package based on the given package weight. */
        fun weight(packageWeight: Double): Double = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Double) : Measurement {

        /** Calculates the weight of the serving based on the given serving weight. */
        fun weight(servingWeight: Double): Double = servingWeight * quantity
    }
}

fun Measurement.weight(food: Food): Double? =
    when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> food.totalWeight?.let(::weight)
        is Measurement.Serving -> food.servingWeight?.let(::weight)
    }

fun Measurement.weight(recipe: Recipe): Double =
    when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> recipe.totalWeight.let(::weight)
        is Measurement.Serving -> recipe.servingWeight.let(::weight)
    }
