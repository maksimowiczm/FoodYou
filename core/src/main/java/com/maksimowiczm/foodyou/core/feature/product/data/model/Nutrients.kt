package com.maksimowiczm.foodyou.core.feature.product.data.model

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
    fun calories(weight: Float): Float {
        return calories * weight / 100
    }

    fun proteins(weight: Float): Float {
        return proteins * weight / 100
    }

    fun carbohydrates(weight: Float): Float {
        return carbohydrates * weight / 100
    }

    fun sugars(weight: Float): Float? {
        return sugars?.times(weight / 100)
    }

    fun fats(weight: Float): Float {
        return fats * weight / 100
    }

    fun saturatedFats(weight: Float): Float? {
        return saturatedFats?.times(weight / 100)
    }

    fun salt(weight: Float): Float? {
        return salt?.times(weight / 100)
    }

    fun sodium(weight: Float): Float? {
        return sodium?.times(weight / 100)
    }

    fun fiber(weight: Float): Float? {
        return fiber?.times(weight / 100)
    }
}
