package com.maksimowiczm.foodyou.feature.diary.data.model

data class Recipe(
    val id: Long,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>
)

data class RecipeIngredient(
    val id: Long,
    override val product: Product,
    override val measurement: WeightMeasurement
) : ProductWithMeasurement
