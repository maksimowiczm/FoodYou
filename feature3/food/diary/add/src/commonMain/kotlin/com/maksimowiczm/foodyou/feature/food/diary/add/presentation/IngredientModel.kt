package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

@Immutable
internal data class IngredientModel(
    val name: String,
    val nutritionFacts: NutritionFacts?,
    val measurement: Measurement,
) {
    constructor(
        ingredient: RecipeIngredient
    ) : this(
        name = ingredient.food.headline,
        nutritionFacts = ingredient.nutritionFacts,
        measurement = ingredient.measurement,
    )
}
