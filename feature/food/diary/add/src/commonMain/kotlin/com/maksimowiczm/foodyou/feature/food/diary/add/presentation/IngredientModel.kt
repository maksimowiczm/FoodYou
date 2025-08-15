package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

@Immutable
internal data class IngredientModel(
    val foodId: FoodId,
    val name: String,
    val nutritionFacts: NutritionFacts?,
    val measurement: Measurement,
    val isRecipe: Boolean,
    val totalWeight: Double?,
    val servingWeight: Double?,
    val isLiquid: Boolean,
) {
    constructor(
        ingredient: RecipeIngredient
    ) : this(
        foodId = ingredient.food.id,
        name = ingredient.food.headline,
        nutritionFacts = ingredient.nutritionFacts,
        measurement = ingredient.measurement,
        isRecipe = ingredient.food is Recipe,
        totalWeight = ingredient.food.totalWeight,
        servingWeight = ingredient.food.servingWeight,
        isLiquid = ingredient.food.isLiquid,
    )
}
