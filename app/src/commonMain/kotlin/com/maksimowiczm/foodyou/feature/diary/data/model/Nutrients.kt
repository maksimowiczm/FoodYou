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
    fun calories(weight: Float): Float = calories * weight / 100

    fun proteins(weight: Float): Float = proteins * weight / 100

    fun carbohydrates(weight: Float): Float = carbohydrates * weight / 100

    fun sugars(weight: Float): Float? = sugars?.times(weight / 100)

    fun fats(weight: Float): Float = fats * weight / 100

    fun saturatedFats(weight: Float): Float? = saturatedFats?.times(weight / 100)

    fun salt(weight: Float): Float? = salt?.times(weight / 100)

    fun sodium(weight: Float): Float? = sodium?.times(weight / 100)

    fun fiber(weight: Float): Float? = fiber?.times(weight / 100)
}
