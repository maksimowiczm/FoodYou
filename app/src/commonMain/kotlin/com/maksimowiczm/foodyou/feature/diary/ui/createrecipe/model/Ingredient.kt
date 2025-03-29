package com.maksimowiczm.foodyou.feature.diary.ui.createrecipe.model

data class Ingredient(
    val name: String,
    val brand: String?,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int
)
