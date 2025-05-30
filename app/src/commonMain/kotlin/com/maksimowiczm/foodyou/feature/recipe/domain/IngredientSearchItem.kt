package com.maksimowiczm.foodyou.feature.recipe.domain

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Gram
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Serving

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
                is Gram -> value
                is Measurement.Package -> food.packageWeight?.let { weight(it) }
                is Serving -> food.servingWeight?.let { weight(it) }
            }
        }
}
