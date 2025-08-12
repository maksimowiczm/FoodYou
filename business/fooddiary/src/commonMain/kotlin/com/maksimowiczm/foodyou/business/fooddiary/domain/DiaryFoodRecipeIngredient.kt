package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

/**
 * Represents an ingredient in a food recipe within the food diary.
 *
 * @param food The food item that is part of the recipe.
 * @param measurement The measurement of the food item used in the recipe.
 */
data class DiaryFoodRecipeIngredient(val food: DiaryFood, val measurement: Measurement) {

    /**
     * Total nutrition facts for the ingredient based on the food's nutrition facts and the weight
     * of the measurement.
     */
    val nutritionFacts: NutritionFacts by lazy { weight.div(100).let { food.nutritionFacts * it } }

    /** The weight of the ingredient based on the measurement provided. */
    val weight: Double
        get() = measurement.weight(food)
}
