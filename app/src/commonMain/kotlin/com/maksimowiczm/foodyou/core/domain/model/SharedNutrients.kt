package com.maksimowiczm.foodyou.core.domain.model

data class SharedNutrients(
    val calories: Float?,
    val proteins: Float?,
    val carbohydrates: Float?,
    val sugars: Float?,
    val fats: Float,
    val saturatedFats: Float?,
    val salt: Float?,
    val sodium: Float?,
    val fiber: Float?
)
