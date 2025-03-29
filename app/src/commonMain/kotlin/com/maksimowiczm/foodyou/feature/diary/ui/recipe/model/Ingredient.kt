package com.maksimowiczm.foodyou.feature.diary.ui.recipe.model

import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement

data class Ingredient(val product: Product, val weightMeasurement: WeightMeasurement) {
    val weight
        get() = weightMeasurement.getWeight(product)

    val calories
        get() = product.nutrients.calories * weight / 100f

    val proteins
        get() = product.nutrients.proteins * weight / 100f

    val carbohydrates
        get() = product.nutrients.carbohydrates * weight / 100f

    val fats
        get() = product.nutrients.fats * weight / 100f
}
