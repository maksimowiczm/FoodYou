package com.maksimowiczm.foodyou.core.model

data class RecipeIngredient(val food: Food, val measurement: Measurement) {
    val weight: Float?
        get() = measurement.weight(food)
}
