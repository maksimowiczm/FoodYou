package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

data class RecipeIngredient(val food: Food, val measurement: Measurement) {
    val weight: Double?
        get() = measurement.weight(food)

    /** Returns the nutrition facts for this ingredient based on its measurement. */
    val nutritionFacts: NutritionFacts?
        get() {
            val weight = weight ?: return null
            return food.nutritionFacts * weight / 100.0
        }
}
