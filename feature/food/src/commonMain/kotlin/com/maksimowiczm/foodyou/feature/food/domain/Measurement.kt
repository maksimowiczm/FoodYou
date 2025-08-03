package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

fun Measurement.weight(food: Food): Float? = when (this) {
    is Measurement.Gram -> value
    is Measurement.Milliliter -> value
    is Measurement.Package -> food.totalWeight?.let(::weight)
    is Measurement.Serving -> food.servingWeight?.let(::weight)
}

fun Measurement.weight(recipe: Recipe): Float = when (this) {
    is Measurement.Gram -> value
    is Measurement.Milliliter -> value
    is Measurement.Package -> recipe.totalWeight.let(::weight)
    is Measurement.Serving -> recipe.servingWeight.let(::weight)
}

fun Measurement.weight(food: FoodSearch.Product): Float? = when (this) {
    is Measurement.Gram -> value
    is Measurement.Milliliter -> value
    is Measurement.Package -> food.totalWeight?.let(::weight)
    is Measurement.Serving -> food.servingWeight?.let(::weight)
}
