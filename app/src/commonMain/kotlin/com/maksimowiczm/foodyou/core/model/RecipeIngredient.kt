package com.maksimowiczm.foodyou.core.model

data class RecipeIngredient(val product: Product, val measurement: Measurement) {
    val weight: Float?
        get() = measurement.weight(product)
}
