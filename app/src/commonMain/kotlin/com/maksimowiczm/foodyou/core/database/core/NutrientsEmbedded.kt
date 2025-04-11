package com.maksimowiczm.foodyou.core.database.core

data class NutrientsEmbedded(
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: Float?,
    val fats: Float,
    val saturatedFats: Float?,
    val salt: Float?,
    val sodium: Float?,
    val fiber: Float?
)
