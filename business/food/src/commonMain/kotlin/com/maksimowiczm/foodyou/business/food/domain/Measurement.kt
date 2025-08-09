package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

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
