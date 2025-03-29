package com.maksimowiczm.foodyou.feature.diary.data.model

/**
 * Nutritional values of the product per 100 grams.
 */
data class Nutrients(
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: NutrientValue,
    val fats: Float,
    val saturatedFats: NutrientValue,
    val salt: NutrientValue,
    val sodium: NutrientValue,
    val fiber: NutrientValue
) {
    val isComplete: Boolean
        get() = listOf(
            sugars,
            saturatedFats,
            salt,
            sodium,
            fiber
        ).all { it is NutrientValue.Complete }
}
