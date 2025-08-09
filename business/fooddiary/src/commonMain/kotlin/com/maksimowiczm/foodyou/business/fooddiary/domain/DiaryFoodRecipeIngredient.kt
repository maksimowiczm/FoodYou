package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

data class DiaryFoodRecipeIngredient(val food: DiaryFood, val measurement: Measurement) {
    val nutritionFacts: NutritionFacts by lazy { weight.div(100).let { food.nutritionFacts * it } }

    val weight: Double
        get() = measurement.weight(food)
}
