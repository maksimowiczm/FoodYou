package com.maksimowiczm.foodyou.feature.recipe.domain

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement

@Immutable
internal data class IngredientSearchItem(
    val food: Food,
    val measurement: Measurement,
    val uniqueId: String
) {
    val headline: String
        get() = food.headline

    val proteins: Float
        get() = food.nutritionFacts.proteins.value

    val carbohydrates: Float
        get() = food.nutritionFacts.carbohydrates.value

    val fats: Float
        get() = food.nutritionFacts.fats.value

    val calories: Float
        get() = food.nutritionFacts.calories.value

    val weight: Float?
        get() = with(measurement) {
            when (this) {
                is Measurement.Gram -> value
                is Measurement.Milliliter -> value
                is Measurement.Package -> food.totalWeight?.let { weight(it) }
                is Measurement.Serving -> food.servingWeight?.let { weight(it) }
            }
        }
}
