package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.data.Food

data class Recipe(
    override val id: FoodId.Recipe,
    override val name: String,
    override val nutrients: Nutrients,
    override val packageWeight: Float?,
    override val servingWeight: Float?,
    val ingredients: List<RecipeIngredient>
) : Food {
    override val brand: String? = null
    override val weightUnit: WeightUnit = WeightUnit.Gram
}
