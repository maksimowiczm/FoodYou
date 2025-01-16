package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.MealProductWithProduct

data class MealProduct(
    val product: Product,

    /**
     * Amount of product in grams
     */
    val weight: Float
) {
    val totalCalories: Float get() = product.calories * weight / 100
}

fun MealProductWithProduct.toDomain(): MealProduct {
    return MealProduct(
        product = product.toDomain(),
        weight = mealProduct.weight
    )
}
