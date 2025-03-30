package com.maksimowiczm.foodyou.feature.diary.ui.recipe.model

import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement

data class IngredientSearch(
    val productId: FoodId.Product,
    val name: String,
    val brand: String?,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val weightMeasurement: WeightMeasurement,
    val selected: Boolean
)
