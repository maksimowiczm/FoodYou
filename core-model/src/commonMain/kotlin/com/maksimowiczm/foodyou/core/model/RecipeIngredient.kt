package com.maksimowiczm.foodyou.core.model

data class RecipeIngredient(val food: Food, val measurement: Measurement) {
    val weight: Float?
        get() = measurement.weight(food)

    /**
     * Returns the nutrition facts for this ingredient based on its measurement.
     */
    val nutritionFacts: NutritionFacts?
        get() {
            val weight = weight ?: return null
            return food.nutritionFacts * weight / 100f
        }
}
