package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

/**
 * Represents an ingredient in a recipe.
 *
 * @param food The food item that is part of the recipe.
 * @param measurement The measurement of the food item used in the recipe.
 */
data class RecipeIngredient(val food: Food, val measurement: Measurement) {

    /**
     * Returns the weight of the ingredient based on the measurement provided. This is calculated by
     * applying the measurement to the food item.
     */
    val weight: Double?
        get() = food.weight(measurement)

    /** Returns the nutrition facts for this ingredient based on its measurement. */
    val nutritionFacts: NutritionFacts?
        get() {
            val weight = weight ?: return null
            return food.nutritionFacts * weight / 100.0
        }
}
