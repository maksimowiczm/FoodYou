package com.maksimowiczm.foodyou.feature.diary.data.model

/**
 * Nutritional values of the product per 100 grams.
 */
data class Nutrients(
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: Float? = null,
    val fats: Float,
    val saturatedFats: Float? = null,
    val salt: Float? = null,
    val sodium: Float? = null,
    val fiber: Float? = null
) {
    // Required fields
    fun calories(weight: Float): Float = calories * weight / 100
    fun proteins(weight: Float): Float = proteins * weight / 100
    fun carbohydrates(weight: Float): Float = carbohydrates * weight / 100
    fun fats(weight: Float): Float = fats * weight / 100

    // Optional fields
    fun get(nutrient: Nutrient, weight: Float): Float? = when (nutrient) {
        Nutrient.Calories -> calories(weight)
        Nutrient.Proteins -> proteins(weight)
        Nutrient.Carbohydrates -> carbohydrates(weight)
        Nutrient.Sugars -> sugars?.times(weight)?.div(100)
        Nutrient.Fats -> fats(weight)
        Nutrient.SaturatedFats -> saturatedFats?.times(weight)?.div(100)
        Nutrient.Salt -> salt?.times(weight)?.div(100)
        Nutrient.Sodium -> sodium?.times(weight)?.div(100)
        Nutrient.Fiber -> fiber?.times(weight)?.div(100)
    }
}

enum class Nutrient {
    Calories,
    Proteins,
    Carbohydrates,
    Sugars,
    Fats,
    SaturatedFats,
    Salt,
    Sodium,
    Fiber
}
