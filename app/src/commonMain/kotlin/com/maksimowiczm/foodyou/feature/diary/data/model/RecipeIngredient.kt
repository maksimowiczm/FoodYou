package com.maksimowiczm.foodyou.feature.diary.data.model

data class RecipeIngredient(val product: Product, val weightMeasurement: WeightMeasurement) {
    val weight: Float
        get() = weightMeasurement.getWeight(product)
}
